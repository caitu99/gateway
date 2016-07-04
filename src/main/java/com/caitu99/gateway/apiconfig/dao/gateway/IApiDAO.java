package com.caitu99.gateway.apiconfig.dao.gateway;


import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IApiDAO extends IEntityDAO<CarmenApi, CarmenApi> {

    /**
     *
     * @param namespace
     * @param name
     * @param version
     * @param env
     * @return
     */
    CarmenApi getRecordByCondition(@Param("namespace") String namespace, @Param("name") String name,
                                   @Param("version") String version, @Param("env") byte env);

    /**
     * 获取所有env下记录
     */
    List<CarmenApi> getRecordByEnv(@Param("env") byte env);

    /**
     * 获取env下分组记录
     */
    List<CarmenApi> getRecordByEnvGroup(@Param("env") byte env, @Param("apiGroup") String apiGroup);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(@Param("id") long id);


    /**
     *
     * @return 获取所有删除的记录
     */
    List<CarmenApi> getAllDeletedRecord();
}