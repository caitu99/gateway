package com.caitu99.gateway.apiconfig.service;

import com.caitu99.gateway.apiconfig.model.CarmenApiMethodMapping;

import java.util.List;

/**
 * Created by chenyun on 15/7/9.
 */
public interface ICarmenApiMethodMappingService {

    final String prefix = "Carmen_api_method_mapping_";

    long insert(CarmenApiMethodMapping mapping);

    void update(CarmenApiMethodMapping mapping);

    void deleteById(long id);

    CarmenApiMethodMapping getById(long id);

    /**
     * 根据appId获取映射关系
     */
    CarmenApiMethodMapping getByApiId(long apiId, byte env);

    /**
     * 根据serviceMethodId获取映射关系
     */
    CarmenApiMethodMapping getByServiceMethodId(long serviceMethodId, byte env);

    /**
     * 根据appId&serviceMethodId获取映射关系
     */
    CarmenApiMethodMapping getByIds(long appId, long serviceMethodId, byte env);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(long id);

    /**
     * 选取某个环境变量下面的所有apiId
     * @param env
     * @return
     */
    List<Long> getApiIdList(byte env);
}
