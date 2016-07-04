package com.caitu99.gateway.apiconfig.service.imp;

import com.alibaba.fastjson.JSON;

import com.caitu99.gateway.apiconfig.dao.gateway.IServiceMethodParamDAO;
import com.caitu99.gateway.apiconfig.model.CarmenServiceMethodParam;
import com.caitu99.gateway.apiconfig.service.ICarmenServiceMethodParamService;
import com.caitu99.gateway.cache.RedisOperate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/9.
 */
@Service
public class CarmenServiceMethodParamService implements ICarmenServiceMethodParamService {

    @Resource
    private IServiceMethodParamDAO serviceMethodParamDAO;

    @Resource
    private RedisOperate redis;

    @Override
    public long insert(CarmenServiceMethodParam serviceMethodParam) {
        serviceMethodParamDAO.save(serviceMethodParam);
        long id = serviceMethodParam.getId();
        /**
         * 缓存处理
         * 全部清除
         */
        String listRecordPrefix =
                prefix + "_" + serviceMethodParam.getServiceMethodId() + "_" + serviceMethodParam.getEnv();
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(listRecordPrefix);
        redis.del(singleRecordPrefix);
        return id;
    }

    @Override
    public void update(CarmenServiceMethodParam serviceMethodParam) {
        serviceMethodParamDAO.update(serviceMethodParam);
        /**
         * 缓存处理
         * 全部清除
         */
        serviceMethodParam = getById(serviceMethodParam.getId());
        if (serviceMethodParam!=null) {
            String listRecordPrefix =
                    prefix + "_" + serviceMethodParam.getServiceMethodId() + "_" + serviceMethodParam.getEnv();
            String singleRecordPrefix = prefix + "_" + serviceMethodParam.getId();
            redis.del(listRecordPrefix);
            redis.del(singleRecordPrefix);
        }
    }

    @Override
    public void deleteById(long id) {//增加缓存清除逻辑,listRecordPrefix，singleRecordPrefix
        CarmenServiceMethodParam param=getById(id);
        if (param!=null){
            String listRecordPrefix = prefix + "_" + param.getServiceMethodId() + "_" + param.getEnv();
            redis.del(listRecordPrefix);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        serviceMethodParamDAO.deleteById(id);
    }

    @Override
    public CarmenServiceMethodParam getById(long id) {
        String singleRecordPrefix = prefix + "_" + id;
        CarmenServiceMethodParam serviceMethodParam = null;
        String context = redis.getStringByKey(singleRecordPrefix);
        if (context != null) {
            serviceMethodParam = JSON.parseObject(context, CarmenServiceMethodParam.class);
        } else {
            serviceMethodParam = serviceMethodParamDAO.getById(id);
            if (serviceMethodParam != null) {
                redis.set(singleRecordPrefix, JSON.toJSONString(serviceMethodParam));
            }
        }
        return serviceMethodParam;
    }

    @Override
    public List<CarmenServiceMethodParam> getByServiceMethodId(long serviceMethodId, byte env) {
        String listRecordPrefix = prefix + "_" + serviceMethodId + "_" + env;
        List<String> listStr = redis.getListAll(listRecordPrefix, 0, -1);
        List<CarmenServiceMethodParam> methodParamList = new ArrayList<>();
        if (listStr.size() != 0) {
            for (String str : listStr) {
                CarmenServiceMethodParam param =
                        JSON.parseObject(str, CarmenServiceMethodParam.class);
                methodParamList.add(param);
            }
        } else {
            methodParamList = serviceMethodParamDAO.getByServiceMethodId(serviceMethodId, env);
            redis.del(listRecordPrefix);//??
            for (CarmenServiceMethodParam methodParam : methodParamList) {
                redis.listAdd(listRecordPrefix, JSON.toJSONString(methodParam));
                redis.set(prefix + "_" + methodParam.getId(), JSON.toJSONString(methodParam));
            }

        }
        return methodParamList;
    }

    @Override
    public void batchSave(List<CarmenServiceMethodParam> list) {
        if (list.size()>0) {
            serviceMethodParamDAO.batchSave(list);
        }
    }

    @Override
    public void physicalDelete(long id) {
        CarmenServiceMethodParam param=getById(id);
        if (param!=null){
            String listRecordPrefix = prefix + "_" + param.getServiceMethodId() + "_" + param.getEnv();
            redis.del(listRecordPrefix);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        serviceMethodParamDAO.physicalDelete(id);
    }
}
