package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.model.CarmenApi;

import java.util.List;

/**
 * Created by chenyun on 15/7/9.
 */
public interface ICarmenApiService {

    final String prefix = "carmen_api_";

    long insert(CarmenApi config);

    void update(CarmenApi config);

    void deleteById(long id);

    CarmenApi getById(long id);//Todo--缺少环境变量

    /**
     * 根据nameSpace&name&version获取记录
     */
    CarmenApi getRecordByCondition(String nameSpace, String name, String version, byte env);


    /**
     * 获取所有env下记录
     */
    List<CarmenApi> getRecordByEnv(byte env);

    /**
     *
     * @param env
     * @param apiGroup
     * @return
     */
    List<CarmenApi> getRecordByEnvGroup(byte env, String apiGroup);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(long id);


    /**
     *
     * @return 获取所有删除的记录
     */
    List<CarmenApi> getAllDeletedRecord();

}
