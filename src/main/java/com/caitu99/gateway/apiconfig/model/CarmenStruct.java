package com.caitu99.gateway.apiconfig.model;

import java.util.Date;

public class CarmenStruct {

    private Long id;

    private Long serviceMethodId;

    private String className;

    private String fieldName;

    private String fieldType;

    private Byte isStructure;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    private String isDelete;

    private Byte env;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceMethodId() {
        return serviceMethodId;
    }

    public void setServiceMethodId(Long serviceMethodId) {
        this.serviceMethodId = serviceMethodId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public Byte getIsStructure() {
        return isStructure;
    }

    public void setIsStructure(Byte isStructure) {
        this.isStructure = isStructure;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
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