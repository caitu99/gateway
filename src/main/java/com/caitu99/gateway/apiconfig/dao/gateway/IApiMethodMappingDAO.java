package com.caitu99.gateway.apiconfig.dao.gateway;

import com.caitu99.gateway.apiconfig.model.CarmenApiMethodMapping;
import com.caitu99.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IApiMethodMappingDAO
        extends IEntityDAO<CarmenApiMethodMapping, CarmenApiMethodMapping> {

    CarmenApiMethodMapping getByApiId(@Param("apiId") long apiId, @Param("env") byte env);

    CarmenApiMethodMapping getByServiceMethodId(@Param("serviceMethodId") long serviceMethodId,
                                                @Param("env") byte env);

    CarmenApiMethodMapping getByIds(@Param("apiId") long apiId,
                                    @Param("serviceMethodId") long serviceMethodId,
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
    List<Long> getApiIdList(@Param("env") byte env);
}