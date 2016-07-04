package com.caitu99.gateway.apiconfig.dao.gateway;


import com.caitu99.gateway.apiconfig.model.CarmenParamMapping;
import com.caitu99.gateway.apiconfig.model.CarmenParamMappingFilter;
import com.caitu99.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IParamMappingDAO extends IEntityDAO<CarmenParamMapping, CarmenParamMapping> {

    /**
     * 根据方法mapping找到serviceMethodId
     */
    CarmenParamMapping getByCondition(@Param("fieldName") String fieldName,
                                      @Param("fieldType") String fieldType,
                                      @Param("serviceMethodId") long serviceMethodId,
                                      @Param("env") byte env);


    List<CarmenParamMapping> getByServiceMethodId(@Param("serviceMethodId") long serviceMethodId,
                                                  @Param("env") byte env);

    List<CarmenParamMapping> getRecordByCondition(@Param("namespace") String namespace,
                                                  @Param("name") String name,
                                                  @Param("version") String version,
                                                  @Param("env") byte env);

    void batchSave(@Param("list") List<CarmenParamMapping> list);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(@Param("id") long id);


    List<CarmenParamMappingFilter> getParamMappingFilter(@Param("env") byte env);

}