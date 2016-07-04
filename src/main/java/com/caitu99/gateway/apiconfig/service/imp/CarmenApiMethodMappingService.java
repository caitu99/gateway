package com.caitu99.gateway.apiconfig.service.imp;


import com.alibaba.fastjson.JSON;

import com.caitu99.gateway.apiconfig.dao.gateway.IApiMethodMappingDAO;
import com.caitu99.gateway.apiconfig.model.CarmenApiMethodMapping;
import com.caitu99.gateway.apiconfig.service.ICarmenApiMethodMappingService;
import com.caitu99.gateway.cache.RedisOperate;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/9.
 */
@Service
public class CarmenApiMethodMappingService implements ICarmenApiMethodMappingService {

    @Resource
    private IApiMethodMappingDAO apiMethodMappingDAO;

    @Resource
    private RedisOperate redis;

    @Override
    public long insert(CarmenApiMethodMapping mapping) {
        apiMethodMappingDAO.save(mapping);
        long id = mapping.getId();
        /**
         * 缓存处理
         * 全部清除
         */
        String key = prefix + "_" + mapping.getApiId() + "_" + mapping.getEnv();
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(key);
        redis.del(singleRecordPrefix);

        return id;
    }

    @Override
    public void update(CarmenApiMethodMapping mapping) {
        apiMethodMappingDAO.update(mapping);
        /**
         * 缓存处理
         * 全部清除
         */
        mapping = getById(mapping.getId());
        if (mapping!=null) {
            String key = prefix + "_" + mapping.getApiId() + "_" + mapping.getEnv();
            String singleRecordPrefix = prefix + "_" + mapping.getId();
            redis.del(key);
            redis.del(singleRecordPrefix);
        }
    }

    @Override
    public void deleteById(long id) {
        CarmenApiMethodMapping mapping = getById(id);
        if (mapping != null) {
            String listRecordPrefix = prefix + "_" + mapping.getApiId() + "_" + mapping.getEnv();
            redis.del(listRecordPrefix);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        apiMethodMappingDAO.deleteById(id);
    }

    @Override
    public CarmenApiMethodMapping getById(long id) {
        String key = prefix + "_" + id;
        String context = redis.getStringByKey(key);
        CarmenApiMethodMapping mapping = null;
        if (context != null) {
            mapping = JSON.parseObject(context, CarmenApiMethodMapping.class);
        } else {
            mapping = apiMethodMappingDAO.getById(id);
            if (mapping != null) {
                redis.set(key, JSON.toJSONString(mapping));
            }
        }
        return mapping;
    }

    @Override
    public CarmenApiMethodMapping getByApiId(long apiId, byte env) {//调用频繁加redis缓存
        String key = prefix + "_" + apiId + "_" + env;
        String context = redis.getStringByKey(key);
        CarmenApiMethodMapping mapping = null;
        if (context != null) {
            mapping = JSON.parseObject(context, CarmenApiMethodMapping.class);
        } else {
            mapping = apiMethodMappingDAO.getByApiId(apiId, env);
            if (mapping != null) {
                redis.set(key, JSON.toJSONString(mapping));
            }
        }
        return mapping;
    }

    @Override
    public CarmenApiMethodMapping getByServiceMethodId(long serviceMethodId, byte env) {
        CarmenApiMethodMapping mapping = null;
        mapping = apiMethodMappingDAO.getByServiceMethodId(serviceMethodId, env);
        return mapping;
    }

    @Override
    public CarmenApiMethodMapping getByIds(long apiId, long serviceMethodId, byte env) {
        CarmenApiMethodMapping mapping = null;
        mapping = apiMethodMappingDAO.getByIds(apiId, serviceMethodId, env);
        return mapping;
    }

    @Override
    public void physicalDelete(long id) {
        CarmenApiMethodMapping mapping = getById(id);
        if (mapping != null) {
            String listRecordPrefix = prefix + "_" + mapping.getApiId() + "_" + mapping.getEnv();
            redis.del(listRecordPrefix);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        apiMethodMappingDAO.physicalDelete(id);
    }

    @Override
    public List<Long> getApiIdList(byte env) {
        return apiMethodMappingDAO.getApiIdList(env);
    }
}
