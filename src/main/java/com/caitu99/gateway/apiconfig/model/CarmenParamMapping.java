package com.caitu99.gateway.apiconfig.model;

import java.util.Date;

public class CarmenParamMapping {

    private Long id;

    private String fieldName;

    private String fieldType;

    private Long serviceMethodId;

    private String apiParamName;

    private Integer dataFrom;

    private String apiNamespace;

    private String apiName;

    private Integer sequence;

    private String version;

    private String methodParamRef;

    private Date createTime;

    private String creator;

    private Date modifyTime;

    private String modifier;

    private String isDelete;

    private Byte env;

    public Integer getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(Integer dataFrom) {
        this.dataFrom = dataFrom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Long getServiceMethodId() {
        return serviceMethodId;
    }

    public void setServiceMethodId(Long serviceMethodId) {
        this.serviceMethodId = serviceMethodId;
    }

    public String getApiParamName() {
        return apiParamName;
    }

    public void setApiParamName(String apiParamName) {
        this.apiParamName = apiParamName;
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


    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethodParamRef() {
        return methodParamRef;
    }

    public void setMethodParamRef(String methodParamRef) {
        this.methodParamRef = methodParamRef;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public Byte getEnv() {
        return env;
    }

    public void setEnv(Byte env) {
        this.env = env;
    }
}