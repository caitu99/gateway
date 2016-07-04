package com.caitu99.gateway.apiconfig.model;

import java.util.Date;

public class CarmenApiParam {

    private Long id;

    private Long apiId;

    private String paramName;

    private Integer sequence;

    private String paramType;

    private Byte isRequired;

    private String rule;

    private Byte isStructure;

    private String defaultValue;

    private String describle;

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

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public Byte getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Byte isRequired) {
        this.isRequired = isRequired;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Byte getIsStructure() {
        return isStructure;
    }

    public void setIsStructure(Byte isStructure) {
        this.isStructure = isStructure;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
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