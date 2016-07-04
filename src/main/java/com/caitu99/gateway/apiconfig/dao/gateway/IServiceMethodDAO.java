package com.caitu99.gateway.apiconfig.dao.gateway;

import com.caitu99.gateway.apiconfig.model.CarmenServiceMethod;
import com.caitu99.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IServiceMethodDAO
        extends IEntityDAO<CarmenServiceMethod, CarmenServiceMethod> {

    /**
     *
     * @param name
     * @param method
     * @param version
     * @param env
     * @return
     */
    CarmenServiceMethod getRecordByCondition(@Param("name") String name,
                                             @Param("method") String method,
                                             @Param("version") String version,
                                             @Param("env") byte env);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(@Param("id") long id);


    /**
     * 选取某个环境变量下面的所有apiId
     * @param env
     * @return
     */
    List<Long> getMethodIdList(@Param("env") byte env);


}