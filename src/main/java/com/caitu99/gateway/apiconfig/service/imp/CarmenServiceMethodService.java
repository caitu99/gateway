package com.caitu99.gateway.apiconfig.service.imp;


import com.alibaba.fastjson.JSON;

import com.caitu99.gateway.apiconfig.dao.gateway.IServiceMethodDAO;
import com.caitu99.gateway.apiconfig.model.CarmenServiceMethod;
import com.caitu99.gateway.apiconfig.service.ICarmenServiceMethodService;
import com.caitu99.gateway.cache.RedisOperate;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/9.
 */
@Service
public class CarmenServiceMethodService implements ICarmenServiceMethodService {

    @Resource
    private IServiceMethodDAO serviceMethodDAO;

    @Resource
    private RedisOperate redis;

    @Override
    public long insert(CarmenServiceMethod serviceMethod) {
        serviceMethodDAO.save(serviceMethod);
        long id = serviceMethod.getId();
        /**
         * 缓存处理-全部清除
         */
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        return id;
    }

    @Override
    public void update(CarmenServiceMethod serviceMethod) {
        serviceMethodDAO.update(serviceMethod);
        /**
         * 缓存处理-全部清除
         */
        serviceMethod=getById(serviceMethod.getId());
        if (serviceMethod!=null) {
            String singleRecordPrefix = prefix + "_" + serviceMethod.getId();
            redis.del(singleRecordPrefix);
        }
    }

    @Override
    public void deleteById(long id) {
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        serviceMethodDAO.deleteById(id);
    }

    @Override
    public CarmenServiceMethod getById(long id) {
        String singleRecordPrefix = prefix + "_" + id;
        String context = redis.getStringByKey(singleRecordPrefix);
        CarmenServiceMethod serviceMethod = null;
        if (context != null) {
            serviceMethod = JSON.parseObject(context, CarmenServiceMethod.class);
        } else {
            serviceMethod = serviceMethodDAO.getById(id);
            if (serviceMethod != null) {
                redis.set(singleRecordPrefix, JSON.toJSONString(serviceMethod));
            }
        }
        return serviceMethod;
    }

    @Override
    public CarmenServiceMethod getRecordByCondition(String name, String method, String Version,
                                                    byte env) {
        CarmenServiceMethod serviceMethod = null;
        serviceMethod = serviceMethodDAO.getRecordByCondition(name, method, Version, env);
        return serviceMethod;

    }

    @Override
    public void physicalDelete(long id) {
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        serviceMethodDAO.physicalDelete(id);
    }

    @Override
    public List<Long> getMethodIdList(byte env) {
        return serviceMethodDAO.getMethodIdList(env);
    }
}
