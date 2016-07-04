package com.caitu99.gateway.apiconfig.service.imp;


import com.alibaba.fastjson.JSON;

import com.caitu99.gateway.apiconfig.dao.gateway.IApiDAO;
import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.gateway.apiconfig.service.ICarmenApiService;
import com.caitu99.gateway.cache.RedisOperate;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/9.
 */

@Service
public class CarmenApiService implements ICarmenApiService {

    @Resource
    private IApiDAO carmenApiDAO;

    @Resource
    private RedisOperate redis;

    @Override
    public long insert(CarmenApi config) {
        carmenApiDAO.save(config);
        long id = config.getId();
        /**
         * 缓存处理-全部清除
         */
        String key =
                prefix + "_" + config.getNamespace() + "_" + config.getName() +
                        "_" + config.getVersion() + "_" + config.getEnv();
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        redis.del(key);
        return id;
    }

    @Override
    public void update(CarmenApi config) {
        carmenApiDAO.update(config);
        /**
         * 缓存处理-全部清除
         */
        config = getById(config.getId());
        if (config!=null) {
            String key =
                    prefix + "_" + config.getNamespace() + "_" + config.getName() +
                            "_" + config.getVersion() + "_" + config.getEnv();
            String singleRecordPrefix = prefix + "_" + config.getId();
            redis.del(singleRecordPrefix);
            redis.del(key);
        }
    }

    @Override
    public void deleteById(long id) {
        CarmenApi config = getById(id);
        if (config != null) {
            String key =
                    prefix + "_" + config.getNamespace() + "_" + config.getName() +
                            "_" + config.getVersion() + "_" + config.getEnv();
            redis.del(key);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        carmenApiDAO.deleteById(id);
    }

    @Override
    public CarmenApi getById(long id) {
        String singleRecordPrefix = prefix + "_" + id;
        String context = redis.getStringByKey(singleRecordPrefix);
        CarmenApi config = null;
        if (context != null) {
            config = JSON.parseObject(context, CarmenApi.class);
        } else {
            config = carmenApiDAO.getById(id);
            if (config != null) {
                redis.set(singleRecordPrefix, JSON.toJSONString(config));
            }
        }
        return config;
    }

    @Override
    public CarmenApi getRecordByCondition(String nameSpace, String name, String version, byte env) {
        CarmenApi config = null;
        String key = prefix + "_" + nameSpace + "_" + name + "_" + version + "_" + env;
        String context = redis.getStringByKey(key);
        if (context != null) {
            config = JSON.parseObject(context, CarmenApi.class);
        } else {
            config = carmenApiDAO.getRecordByCondition(nameSpace, name, version, env);
            if (config != null) {
                redis.set(key, JSON.toJSONString(config));
            }
        }
        return config;
    }

    @Override
    public List<CarmenApi> getRecordByEnv(byte env) {
        List<CarmenApi> apiList = null;
        apiList = carmenApiDAO.getRecordByEnv(env);
        return apiList;
    }

    @Override
    public List<CarmenApi> getRecordByEnvGroup(byte env, String apiGroup) {
        return carmenApiDAO.getRecordByEnvGroup(env, apiGroup);
    }

    @Override
    public void physicalDelete(long id) {
        CarmenApi config = getById(id);
        if (config != null) {
            String key =
                    prefix + "_" + config.getNamespace() + "_" + config.getName() + "_" + config.getVersion() +
                            "_" + config.getEnv();
            redis.del(key);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        carmenApiDAO.physicalDelete(id);
    }

    @Override
    public List<CarmenApi> getAllDeletedRecord() {
        return carmenApiDAO.getAllDeletedRecord();
    }
}
