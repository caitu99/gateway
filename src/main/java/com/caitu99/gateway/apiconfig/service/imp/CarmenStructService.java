package com.caitu99.gateway.apiconfig.service.imp;


import com.alibaba.fastjson.JSON;

import com.caitu99.gateway.apiconfig.dao.gateway.IStructDAO;
import com.caitu99.gateway.apiconfig.model.CarmenStruct;
import com.caitu99.gateway.apiconfig.service.ICarmenStructService;
import com.caitu99.gateway.cache.RedisOperate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/9.
 */
@Service
public class CarmenStructService implements ICarmenStructService {


    @Resource
    private IStructDAO structDAO;

    @Resource
    private RedisOperate redis;

    @Override
    public long insert(CarmenStruct struct) {
        structDAO.save(struct);
        long id = struct.getId();

        /**
         * 缓存处理
         * 全部清除
         */
        String key = prefix + "_" + struct.getClassName() + "_" + struct.getServiceMethodId() + "_" + struct.getEnv();
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(key);
        redis.del(singleRecordPrefix);
        return id;
    }

    @Override
    public void update(CarmenStruct struct) {
        structDAO.update(struct);
        /**
         * 缓存处理
         * 全部清除
         */
        struct = getById(struct.getId());
        if (struct!=null) {
            String key =
                    prefix + "_" + struct.getClassName() + "_" + struct.getServiceMethodId() + "_" + struct.getEnv();
            String singleRecordPrefix = prefix + "_" + struct.getId();
            redis.del(key);
            redis.del(singleRecordPrefix);
        }
    }

    @Override
    public void deleteById(long id) {
        CarmenStruct struct = getById(id);
        if (struct != null) {
            String key =
                    prefix + "_" + struct.getClassName() + "_" + struct.getServiceMethodId() + "_" + struct.getEnv();
            redis.del(key);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        structDAO.deleteById(id);
    }

    @Override
    public CarmenStruct getById(long id) {

        String singleRecordPrefix = prefix + "_" + id;
        String context = redis.getStringByKey(singleRecordPrefix);
        CarmenStruct struct = null;
        if (context != null) {
            struct = JSON.parseObject(context, CarmenStruct.class);
        } else {
            struct = structDAO.getById(id);
            if (struct != null) {
                redis.set(singleRecordPrefix, JSON.toJSONString(struct));
            }
        }
        return struct;
    }

    @Override
    public List<CarmenStruct> getByClassMethodId(String className, long serviceMethodId, byte env) {
        String key = prefix + "_" + className + "_" + serviceMethodId + "_" + env;
        List<String> listStr = redis.getListAll(key, 0, -1);//获取所有记录
        List<CarmenStruct> structList = new ArrayList<>();
        if (listStr.size() != 0) {
            for (String str : listStr) {
                CarmenStruct struct = JSON.parseObject(str, CarmenStruct.class);
                structList.add(struct);
            }
        } else {
            structList = structDAO.getByClassMethodId(className, serviceMethodId, env);
            redis.del(key);
            for (CarmenStruct struct : structList) {
                redis.listAdd(key, JSON.toJSONString(struct));
                redis.set(prefix + "_" + struct.getId(), JSON.toJSONString(struct));
            }
        }
        return structList;
    }

    @Override
    public List<CarmenStruct> getByClassName(String className, byte env) {
        List<CarmenStruct> structList = null;
        structList = structDAO.getByClassName(className, env);
        return structList;
    }

    @Override
    public List<CarmenStruct> getByServiceMethodId(long serviceMethodId, byte env) {
        List<CarmenStruct> structList = null;
        structList = structDAO.getByServiceMethodId(serviceMethodId, env);
        return structList;
    }

    @Override
    public void batchSave(List<CarmenStruct> list) {
        if (list.size()>0) {
            structDAO.batchSave(list);
        }
    }

    @Override
    public void physicalDelete(long id) {
        CarmenStruct struct = getById(id);
        if (struct != null) {
            String key = prefix + "_" + struct.getClassName() +
                    "_" + struct.getServiceMethodId() + "_" + struct.getEnv();
            redis.del(key);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        structDAO.physicalDelete(id);
    }
}
