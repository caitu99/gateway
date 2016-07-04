package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.model.CarmenStruct;

import java.util.List;

/**
 * Created by chenyun on 15/7/9.
 */
public interface ICarmenStructService {

    final String prefix = "carmen_struct_";

    long insert(CarmenStruct struct);

    void update(CarmenStruct struct);

    void deleteById(long id);

    CarmenStruct getById(long id);

    /**
     * method+类型过滤
     */
    List<CarmenStruct> getByClassMethodId(String className, long serviceMethodId, byte env);

    /**
     * 类型过滤
     */
    List<CarmenStruct> getByClassName(String className, byte env);

    /**
     * method过滤
     */
    List<CarmenStruct> getByServiceMethodId(long serviceMethodId, byte env);

    void batchSave(List<CarmenStruct> list);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(long id);

}
