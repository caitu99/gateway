package com.caitu99.gateway.frequency.service.imp;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.frequency.dao.gateway.IConfigDAO;
import com.caitu99.gateway.frequency.model.CarmenConfig;
import com.caitu99.gateway.frequency.service.ICarmenConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/8/10.
 */
@Service
public class CarmenConfigService implements ICarmenConfigService {

    @Resource
    private RedisOperate redis;

    @Resource
    private IConfigDAO carmenConfigDAO;

    @Override
    public int insert(CarmenConfig config) {
        carmenConfigDAO.save(config);
        int id = config.getId();
        String key = prefix + config.getConfigName();
        String singleRecordPrefix = prefix + id;
        redis.del(key);
        redis.del(singleRecordPrefix);
        return id;
    }

    @Override
    public void update(CarmenConfig config) {
        carmenConfigDAO.update(config);
        config = getById(config.getId());
        String key = prefix + config.getConfigName();
        String singleRecordPrefix = prefix + config.getId();
        redis.del(key);
        redis.del(singleRecordPrefix);
    }

    @Override
    public void deleteById(int id) {
        CarmenConfig config = getById(id);
        if (config != null) {
            String key = prefix + config.getConfigName();
            redis.del(key);
        }
        String singleRecordPrefix = prefix + id;
        redis.del(singleRecordPrefix);
        carmenConfigDAO.deleteById(id);

    }

    @Override
    public CarmenConfig getById(int id) {
        String singleRecordPrefix = prefix + id;
        String context = redis.getStringByKey(singleRecordPrefix);
        CarmenConfig config = null;
        if (context != null) {
            config = JSON.parseObject(context, CarmenConfig.class);
        } else {
            config = carmenConfigDAO.getById(id);
            redis.set(singleRecordPrefix, JSON.toJSONString(config));
        }
        return config;
    }

    @Override
    public CarmenConfig getByConfigName(String configName) {
        CarmenConfig config = null;
        String key = prefix + configName;
        String context = redis.getStringByKey(key);
        if (context != null) {
            config = JSON.parseObject(context, CarmenConfig.class);
        } else {
            config = carmenConfigDAO.getByConfigName(configName);
            if (config != null) {
                redis.set(key, JSON.toJSONString(config));
            }
        }
        return config;
    }
}
