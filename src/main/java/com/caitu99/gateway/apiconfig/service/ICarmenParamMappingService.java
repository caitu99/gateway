package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.model.CarmenParamMapping;
import com.caitu99.gateway.apiconfig.model.CarmenParamMappingFilter;

import java.util.List;

/**
 * Created by chenyun on 15/7/9.
 */
public interface ICarmenParamMappingService {

    final String prefix = "carmen_param_mapping_";

    long insert(CarmenParamMapping mapRecord);

    void update(CarmenParamMapping mapRecord);

    void deleteById(long id);

    CarmenParamMapping getById(long id);

    /**
     * 根据方法mapping找到serviceMethodId
     */
    CarmenParamMapping getByCondition(String fieldName, String fieldType, long serviceMethodId,
                                      byte env);

    /**
     * 根据serviceMethodId获取list
     */
    List<CarmenParamMapping> getByServiceMethodId(long serviceMethodId, byte env);


    List<CarmenParamMapping> getRecordByCondition(String namespace, String name,
                                                  String version, byte env);

    void batchSave(List<CarmenParamMapping> list);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(long id);


    List<CarmenParamMappingFilter> getParamMappingFilter(byte env);
}
