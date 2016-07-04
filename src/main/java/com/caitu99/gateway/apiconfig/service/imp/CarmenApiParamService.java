package com.caitu99.gateway.apiconfig.service.imp;


import com.alibaba.fastjson.JSON;

import com.caitu99.gateway.apiconfig.dao.gateway.IApiParamDAO;
import com.caitu99.gateway.apiconfig.model.CarmenApiParam;
import com.caitu99.gateway.apiconfig.service.ICarmenApiParamService;
import com.caitu99.gateway.cache.RedisOperate;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/9.
 */
@Service
public class CarmenApiParamService implements ICarmenApiParamService {

    @Resource
    private IApiParamDAO carmenApiParamDAO;

    @Resource
    private RedisOperate redis;

    @Override
    public long insert(CarmenApiParam param) {
        carmenApiParamDAO.save(param);
        long id = param.getId();
        /**
         * 缓存处理-全部清除
         */
        String key = prefix + "_" + param.getApiId() + "_" + param.getEnv();
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        redis.del(key);
        return id;
    }

    @Override
    public void update(CarmenApiParam param) {
        carmenApiParamDAO.update(param);

        /**
         * 缓存处理-全部清除
         */
        param = getById(param.getId());
        if (param != null) {
            String key = prefix + "_" + param.getApiId() + "_" + param.getEnv();
            String singleRecordPrefix = prefix + "_" + param.getId();
            redis.del(singleRecordPrefix);
            redis.del(key);
        }

    }

    @Override
    public void deleteById(long id) {
        /**
         * 缓存处理-全部清除
         */
        CarmenApiParam record = getById(id);
        if (record != null) {
            String listRecordPrefix = prefix + "_" + record.getApiId() + "_" + record.getEnv();
            redis.del(listRecordPrefix);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        carmenApiParamDAO.deleteById(id);
    }

    @Override
    public CarmenApiParam getById(long id) {
        String singleRecordPrefix = prefix + "_" + id;
        String context = redis.getStringByKey(singleRecordPrefix);
        CarmenApiParam param = null;
        if (context != null) {
            param = JSON.parseObject(context, CarmenApiParam.class);
        } else {
            param = carmenApiParamDAO.getById(id);
            if (param != null) {
                redis.set(singleRecordPrefix, JSON.toJSONString(param));
            }
        }
        return param;
    }

    @Override
    public List<CarmenApiParam> getByApiId(long apiId, byte env) {
        String listRecordPrefix = prefix + "_" + apiId + "_ " + env;
        String content = redis.getStringByKey(listRecordPrefix);
        List<CarmenApiParam> paramList = new ArrayList<>();
        if (StringUtils.isNotEmpty(content)) {
            paramList = JSON.parseArray(content, CarmenApiParam.class);
        } else {
            paramList = carmenApiParamDAO.getByApiId(apiId, env);
            redis.del(listRecordPrefix);

            if (paramList.size() > 0) {
                content = JSON.toJSONString(paramList);
                redis.set(listRecordPrefix, content);

                for (CarmenApiParam apiParam : paramList) {
                    redis.set(prefix + "_" + apiParam.getId(), JSON.toJSONString(apiParam));
                }
            }
        }
        return paramList;
    }

    @Override
    public void batchSave(List<CarmenApiParam> list) {
        if (list.size()>0) {
            carmenApiParamDAO.batchSave(list);
        }
    }

    @Override
    public void physicalDelete(long id) {
        /**
         * 缓存处理-全部清除
         */
        CarmenApiParam record = getById(id);
        if (record != null) {
            String listRecordPrefix = prefix + "_" + record.getApiId() + record.getEnv();
            redis.del(listRecordPrefix);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        carmenApiParamDAO.physicalDelete(id);
    }

    @Override
    public List<Long> getApiIdList(byte env) {
        return carmenApiParamDAO.getApiIdList(env);
    }
}
