package com.caitu99.gateway.frequency.service.imp;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.frequency.dao.gateway.IFreqConfigDAO;
import com.caitu99.gateway.frequency.model.CarmenFreqConfig;
import com.caitu99.gateway.frequency.service.ICarmenFreqConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by chenyun on 15/8/10.
 */
@Service
public class CarmenFreqConfigService implements ICarmenFreqConfigService {

    @Resource
    private RedisOperate redis;

    @Resource
    private IFreqConfigDAO freqConfigDAO;

    @Override
    public long insert(CarmenFreqConfig config) {
        freqConfigDAO.save(config);
        long id = config.getId();
        /**
         * 缓存处理-全部清除
         */
        String key1 = prefix + config.getType() + config.getApiRef() + config.getClientId();
        String key2 = prefix + config.getType() + config.getApiRef();
        String singleRecordPrefix = prefix + id;
        redis.del(singleRecordPrefix);
        redis.del(key1);
        redis.del(key2);
        return id;
    }

    @Override
    public void update(CarmenFreqConfig config) {
        freqConfigDAO.update(config);
        config = getById(config.getId());
        String key1 = prefix + config.getType() + config.getApiRef() + config.getClientId();
        String key2 = prefix + config.getType() + config.getApiRef();
        String singleRecordPrefix = prefix + config.getId();
        redis.del(singleRecordPrefix);
        redis.del(key1);
        redis.del(key2);
    }

    @Override
    public void deleteById(long id) {
        CarmenFreqConfig config = getById(id);
        if (config != null) {
            String key1 = prefix + config.getType() + config.getApiRef() + config.getClientId();
            String key2 = prefix + config.getType() + config.getApiRef();
            redis.del(key1);
            redis.del(key2);
        }
        String singleRecordPrefix = prefix + id;
        redis.del(singleRecordPrefix);
        freqConfigDAO.deleteById(id);
    }

    @Override
    public CarmenFreqConfig getById(long id) {
        String singleRecordPrefix = prefix + id;
        String context = redis.getStringByKey(singleRecordPrefix);
        CarmenFreqConfig config = null;
        if (context != null) {
            config = JSON.parseObject(context, CarmenFreqConfig.class);
        } else {
            config = freqConfigDAO.getById(id);
            redis.set(singleRecordPrefix, JSON.toJSONString(config));
        }
        return config;
    }

    @Override
    public CarmenFreqConfig getValueByCondition(int type, long apiRef, long clientId) {
        CarmenFreqConfig config = null;
        String key = prefix + type + apiRef + clientId;
        String context = redis.getStringByKey(key);
        if (context != null) {
            config = JSON.parseObject(context, CarmenFreqConfig.class);
        } else {
            config = freqConfigDAO.getValueByCondition(type, apiRef, clientId);
            redis.set(key, JSON.toJSONString(config));
        }
        return config;
    }

    @Override
    public CarmenFreqConfig getValueByApiAndType(int type, long apiRef) {
        CarmenFreqConfig config = null;
        String key = prefix + type + apiRef;
        String context = redis.getStringByKey(key);
        if (context != null) {
            config = JSON.parseObject(context, CarmenFreqConfig.class);
        } else {
            config = freqConfigDAO.getValueByApiAndType(type, apiRef);
            redis.set(key, JSON.toJSONString(config));
        }
        return config;
    }

    @Override
    public List<CarmenFreqConfig> getListValueByCondition(long apiRef, long clientId) {
        return freqConfigDAO.getListValueByCondition(apiRef, clientId);
    }

    @Override
    public List<CarmenFreqConfig> getListValueByApi(long apiRef) {
        return freqConfigDAO.getListValueByApi(apiRef);
    }


}
