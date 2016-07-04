package com.caitu99.gateway.apiconfig.model;

import java.util.List;

/**
 * 数据迁移一个服务关联数据定义 Created by chenyun on 15/7/14.
 */
public class CarmenApiDataMigrate {

    private CarmenApi Api;
    private List<CarmenApiParam> ApiParamList;
    private CarmenServiceMethod serviceMethod;
    private CarmenApiMethodMapping apiMethodMapping;
    private List<CarmenServiceMethodParam> serviceMethodParamList;
    private List<CarmenStruct> structList;
    private List<CarmenParamMapping> paramMappingList;

    public CarmenApi getApi() {
        return Api;
    }

    public CarmenApiMethodMapping getApiMethodMapping() {
        return apiMethodMapping;
    }

    public void setApiMethodMapping(
            CarmenApiMethodMapping apiMethodMapping) {
        this.apiMethodMapping = apiMethodMapping;
    }

    public void setApi(CarmenApi api) {
        Api = api;
    }

    public List<CarmenApiParam> getApiParamList() {
        return ApiParamList;
    }

    public void setApiParamList(
            List<CarmenApiParam> apiParamList) {
        ApiParamList = apiParamList;
    }

    public List<CarmenParamMapping> getParamMappingList() {
        return paramMappingList;
    }

    public void setParamMappingList(
            List<CarmenParamMapping> paramMappingList) {
        this.paramMappingList = paramMappingList;
    }

    public List<CarmenStruct> getStructList() {
        return structList;
    }

    public void setStructList(
            List<CarmenStruct> structList) {
        this.structList = structList;
    }

    public List<CarmenServiceMethodParam> getServiceMethodParamList() {
        return serviceMethodParamList;
    }

    public void setServiceMethodParamList(
            List<CarmenServiceMethodParam> serviceMethodParamList) {
        this.serviceMethodParamList = serviceMethodParamList;
    }

    public CarmenServiceMethod getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(CarmenServiceMethod serviceMethod) {
        this.serviceMethod = serviceMethod;
    }
}
