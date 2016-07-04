package com.caitu99.gateway.apiconfig.service.imp;

import com.alibaba.fastjson.JSON;

import com.caitu99.gateway.apiconfig.dao.gateway.IParamMappingDAO;
import com.caitu99.gateway.apiconfig.model.CarmenParamMapping;
import com.caitu99.gateway.apiconfig.model.CarmenParamMappingFilter;
import com.caitu99.gateway.apiconfig.service.ICarmenParamMappingService;
import com.caitu99.gateway.cache.RedisOperate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/9.
 */
@Service
public class CarmenParamMappingService implements ICarmenParamMappingService {

    @Resource
    private IParamMappingDAO paramMappingDAO;

    @Resource
    private RedisOperate redis;

    @Override
    public long insert(CarmenParamMapping mapRecord) {
        paramMappingDAO.save(mapRecord);
        long id = mapRecord.getId();

        /**
         * 缓存处理
         * 全部清除
         */
        String listRecordPrefix = prefix + "_" + mapRecord.getServiceMethodId() + "_" + mapRecord.getEnv();
        String singleRecordPrefix = prefix + "_" + id;
        String key = prefix + "_" + mapRecord.getFieldName() + "_" + mapRecord.getFieldType() + "_" + mapRecord
                .getServiceMethodId() + "_" + mapRecord.getEnv();
        redis.del(listRecordPrefix);
        redis.del(singleRecordPrefix);
        redis.del(key);
        return id;
    }

    @Override
    public void update(CarmenParamMapping mapRecord) {
        paramMappingDAO.update(mapRecord);

        /**
         * 缓存处理
         * 全部清除
         */
        mapRecord = getById(mapRecord.getId());
        if (mapRecord!=null) {
            String listRecordPrefix = prefix + "_" + mapRecord.getServiceMethodId() + "_" + mapRecord.getEnv();
            String singleRecordPrefix = prefix + "_" + mapRecord.getId();
            String key = prefix + "_" + mapRecord.getFieldName() + "_" + mapRecord.getFieldType() +
                    "_" + mapRecord.getServiceMethodId() + "_" + mapRecord.getEnv();
            redis.del(listRecordPrefix);
            redis.del(singleRecordPrefix);
            redis.del(key);
        }
    }

    @Override
    public void deleteById(long id) {
        CarmenParamMapping mapRecord = getById(id);
        if (mapRecord != null) {
            String listRecordPrefix = prefix + "_" + mapRecord.getServiceMethodId() + "_" + mapRecord.getEnv();
            String key = prefix + "_" + mapRecord.getFieldName() + "_" + mapRecord.getFieldType() +
                    "_" + mapRecord.getServiceMethodId() + "_" + mapRecord.getEnv();
            redis.del(listRecordPrefix);
            redis.del(key);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        paramMappingDAO.deleteById(id);
    }

    @Override
    public CarmenParamMapping getById(long id) {

        String singleRecordPrefix = prefix + "_" + id;
        String context = redis.getStringByKey(singleRecordPrefix);
        CarmenParamMapping paramMapping = null;
        if (context != null) {
            paramMapping = JSON.parseObject(context, CarmenParamMapping.class);
        } else {
            paramMapping = paramMappingDAO.getById(id);
            if (paramMapping != null) {
                redis.set(singleRecordPrefix, JSON.toJSONString(paramMapping));
            }
        }
        return paramMapping;
    }

    @Override
    public CarmenParamMapping getByCondition(String fieldName, String fieldType,
                                             long serviceMethodId,
                                             byte env) {
        String key = prefix + "_" + fieldName + "_" + fieldType + "_" + serviceMethodId + "_" + env;
        String context = redis.getStringByKey(key);
        CarmenParamMapping paramMapping = null;
        if (context != null) {
            paramMapping = JSON.parseObject(context, CarmenParamMapping.class);
        } else {
            paramMapping =
                    paramMappingDAO.getByCondition(fieldName, fieldType, serviceMethodId, env);
            if (paramMapping != null) {
                redis.set(key, JSON.toJSONString(paramMapping));
            }
        }
        return paramMapping;
    }

    @Override
    public List<CarmenParamMapping> getByServiceMethodId(long serviceMethodId, byte env) {

        String listRecordPrefix = prefix + "_" + serviceMethodId + "_" + env;
        List<String> listStr = redis.getListAll(listRecordPrefix, 0, -1);//获取所有记录
        List<CarmenParamMapping> paramMappingList = new ArrayList<>();
        if (listStr.size() != 0) {
            for (String str : listStr) {
                CarmenParamMapping bean = JSON.parseObject(str, CarmenParamMapping.class);
                paramMappingList.add(bean);
            }
        } else {
            paramMappingList = paramMappingDAO.getByServiceMethodId(serviceMethodId, env);
            redis.del(listRecordPrefix);
            for (CarmenParamMapping apiParam : paramMappingList) {
                redis.listAdd(listRecordPrefix, JSON.toJSONString(apiParam));
                redis.set(prefix + "_" + apiParam.getId(), JSON.toJSONString(apiParam));
            }
        }
        return paramMappingList;
    }

    @Override
    public List<CarmenParamMapping> getRecordByCondition(String namespace, String name,
                                                         String version,
                                                         byte env) {
        List<CarmenParamMapping> paramMappingList = null;
        paramMappingList = paramMappingDAO.getRecordByCondition(namespace, name, version, env);
        return paramMappingList;
    }

    @Override
    public void batchSave(List<CarmenParamMapping> list) {
        if (list.size()>0) {
            paramMappingDAO.batchSave(list);
        }
    }

    @Override
    public void physicalDelete(long id) {
        CarmenParamMapping mapRecord = getById(id);
        if (mapRecord != null) {
            String listRecordPrefix = prefix + "_" + mapRecord.getServiceMethodId() + "_" + mapRecord.getEnv();
            String key = prefix + "_" + mapRecord.getFieldName() + "_" + mapRecord.getFieldType() +
                    "_" + mapRecord.getServiceMethodId() + "_" + mapRecord.getEnv();
            redis.del(listRecordPrefix);
            redis.del(key);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        paramMappingDAO.physicalDelete(id);
    }

    @Override
    public List<CarmenParamMappingFilter> getParamMappingFilter(byte env) {
        return paramMappingDAO.getParamMappingFilter(env);
    }
}
