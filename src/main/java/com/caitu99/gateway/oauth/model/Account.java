package com.caitu99.gateway.oauth.model;

import java.util.Date;

public class Account {
    private Long id;

    private Long userId;

    private Long totalIntegral;

    private Long availableIntegral;

    private Long freezeIntegral;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(Long totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public Long getAvailableIntegral() {
        return availableIntegral;
    }

    public void setAvailableIntegral(Long availableIntegral) {
        this.availableIntegral = availableIntegral;
    }

    public Long getFreezeIntegral() {
        return freezeIntegral;
    }

    public void setFreezeIntegral(Long freezeIntegral) {
        this.freezeIntegral = freezeIntegral;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }
}