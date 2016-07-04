package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.model.CarmenApiParam;

import java.util.List;

/**
 * Created by chenyun on 15/7/9.
 */
public interface ICarmenApiParamService {

    final String prefix = "carmen_api_param_";

    long insert(CarmenApiParam param);

    void update(CarmenApiParam param);

    void deleteById(long id);

    CarmenApiParam getById(long id);//Todo--缺少环境变量

    /**
     * 根据apiId外键获取记录
     */
    List<CarmenApiParam> getByApiId(long apiId, byte env);

    /**
     * 批量新增
     */
    void batchSave(List<CarmenApiParam> list);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(long id);


    List<Long> getApiIdList(byte env);

}
