package com.caitu99.gateway.apiconfig.model;

/**
 * Created by chenyun on 15/9/24.
 */
public class CarmenParamMappingFilter {
    private Long serviceMethodId;

    private String apiNamespace;

    private String apiName;

    private String version;

    public Long getServiceMethodId() {
        return serviceMethodId;
    }

    public void setServiceMethodId(Long serviceMethodId) {
        this.serviceMethodId = serviceMethodId;
    }

    public String getApiNamespace() {
        return apiNamespace;
    }

    public void setApiNamespace(String apiNamespace) {
        this.apiNamespace = apiNamespace;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
