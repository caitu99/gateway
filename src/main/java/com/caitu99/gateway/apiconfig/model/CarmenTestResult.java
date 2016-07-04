package com.caitu99.gateway.apiconfig.model;

import java.util.Date;

public class CarmenTestResult {
    private Integer id;

    private String feilds;

    private String feildsValue;

    private Long refApiId;

    private Date createTime;

    private String creator;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeilds() {
        return feilds;
    }

    public void setFeilds(String feilds) {
        this.feilds = feilds;
    }

    public String getFeildsValue() {
        return feildsValue;
    }

    public void setFeildsValue(String feildsValue) {
        this.feildsValue = feildsValue;
    }

    public Long getRefApiId() {
        return refApiId;
    }

    public void setRefApiId(Long refApiId) {
        this.refApiId = refApiId;
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
}