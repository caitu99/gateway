package com.caitu99.gateway.apiconfig.model;

import java.util.Date;

public class OpenResource {

    private Integer id;

    private String uri;

    private String description;

    private String groupAlias;

    private Byte isWrite;

    private Byte isInner;

    private Byte level;

    private Date createTime;

    private Date updateTime;

    private String version;

    private Long refApiId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupAlias() {
        return groupAlias;
    }

    public void setGroupAlias(String groupAlias) {
        this.groupAlias = groupAlias;
    }

    public Byte getIsInner() {
        return isInner;
    }

    public void setIsInner(Byte isInner) {
        this.isInner = isInner;
    }

    public Byte getIsWrite() {
        return isWrite;
    }

    public void setIsWrite(Byte isWrite) {
        this.isWrite = isWrite;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getRefApiId() {
        return refApiId;
    }

    public void setRefApiId(Long refApiId) {
        this.refApiId = refApiId;
    }
}