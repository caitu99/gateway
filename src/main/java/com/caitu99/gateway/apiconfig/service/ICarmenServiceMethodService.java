package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.model.CarmenServiceMethod;

import java.util.List;

/**
 * Created by chenyun on 15/7/9.
 */
public interface ICarmenServiceMethodService {

    final String prefix = "carmen_service_method_";

    long insert(CarmenServiceMethod serviceMethod);

    void update(CarmenServiceMethod serviceMethod);

    void deleteById(long id);

    CarmenServiceMethod getById(long id);//Todo--缺少环境变量

    /**
     * 根据组合键获取一条记录
     */
    CarmenServiceMethod getRecordByCondition(String name, String method, String Version, byte env);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(long id);


    List<Long> getMethodIdList(byte env);

}
