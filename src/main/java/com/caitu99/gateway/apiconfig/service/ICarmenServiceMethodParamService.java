package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.model.CarmenServiceMethodParam;

import java.util.List;

/**
 * Created by chenyun on 15/7/9.
 */
public interface ICarmenServiceMethodParamService {

    final String prefix = "carmen_service_method_param";

    long insert(CarmenServiceMethodParam serviceMethodParam);

    void update(CarmenServiceMethodParam serviceMethodParam);

    void deleteById(long id);

    CarmenServiceMethodParam getById(long id);//todo--缺少环境变量

    /**
     * 根据外键serviceMethodId获取记录
     */
    List<CarmenServiceMethodParam> getByServiceMethodId(long serviceMethodId, byte env);

    void batchSave(List<CarmenServiceMethodParam> list);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(long id);
}
