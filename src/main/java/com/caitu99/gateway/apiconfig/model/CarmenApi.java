package com.caitu99.gateway.apiconfig.model;

import java.util.Date;

public class CarmenApi {

    private Long id;

    private String namespace;

    private String name;

    private String version;

    private String appName;

    private String apiGroup;

    private String apiDesc;

    private String apiScenarios;

    private String addressUrl;

    private Byte enableLog;

    private Byte enableFreq;

    private Byte enableInnerOuter;

    private Byte validFlag;

    private Byte testFlag;

    private Byte migrateFlag;

    private Byte apiType;

    private Byte sessionFlag;

    private String requestType;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    private String isDelete;

    private Byte env;

    public String getApiScenarios() {
        return apiScenarios;
    }

    public void setApiScenarios(String apiScenarios) {
        this.apiScenarios = apiScenarios;
    }

    public String getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(String apiGroup) {
        this.apiGroup = apiGroup;
    }

    public String getApiDesc() {
        return apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        this.apiDesc = apiDesc;
    }

    public Byte getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(Byte validFlag) {
        this.validFlag = validFlag;
    }

    public Byte getTestFlag() {
        return testFlag;
    }

    public void setTestFlag(Byte testFlag) {
        this.testFlag = testFlag;
    }

    public Byte getMigrateFlag() {
        return migrateFlag;
    }

    public void setMigrateFlag(Byte migrateFlag) {
        this.migrateFlag = migrateFlag;
    }

    public Byte getSessionFlag() {
        return sessionFlag;
    }

    public void setSessionFlag(Byte sessionFlag) {
        this.sessionFlag = sessionFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAddressUrl() {
        return addressUrl;
    }

    public void setAddressUrl(String addressUrl) {
        this.addressUrl = addressUrl;
    }

    public Byte getEnableLog() {
        return enableLog;
    }

    public void setEnableLog(Byte enableLog) {
        this.enableLog = enableLog;
    }

    public Byte getEnableFreq() {
        return enableFreq;
    }

    public void setEnableFreq(Byte enableFreq) {
        this.enableFreq = enableFreq;
    }

    public Byte getEnableInnerOuter() {
        return enableInnerOuter;
    }

    public void setEnableInnerOuter(Byte enableInnerOuter) {
        this.enableInnerOuter = enableInnerOuter;
    }

    public Byte getApiType() {
        return apiType;
    }

    public void setApiType(Byte apiType) {
        this.apiType = apiType;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
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