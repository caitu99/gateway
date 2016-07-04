package com.caitu99.gateway.apiconfig.dao.gateway;

import com.caitu99.gateway.apiconfig.model.CarmenStruct;
import com.caitu99.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IStructDAO extends IEntityDAO<CarmenStruct, CarmenStruct> {

    /**
     * method+类型过滤
     */
    List<CarmenStruct> getByClassMethodId(@Param("className") String className,
                                          @Param("serviceMethodId") long serviceMethodId,
                                          @Param("env") byte env);

    /**
     * 类型过滤
     */
    List<CarmenStruct> getByClassName(@Param("className") String className, @Param("env") byte env);

    /**
     * method过滤
     */
    List<CarmenStruct> getByServiceMethodId(@Param("serviceMethodId") long serviceMethodId,
                                            @Param("env") byte env);

    void batchSave(@Param("list") List<CarmenStruct> list);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(@Param("id") long id);
}