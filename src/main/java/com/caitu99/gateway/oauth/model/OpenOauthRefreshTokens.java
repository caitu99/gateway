package com.caitu99.gateway.oauth.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class OpenOauthRefreshTokens {

    private Integer id;

    private String refreshToken;

    private String clientId;

    private Integer userId;

    private String extra;

    private String accessToken;

    private Date expires;

    private String scope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JSONField(name = "refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    @JSONField(name = "refresh_token")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @JSONField(name = "client_id")
    public String getClientId() {
        return clientId;
    }

    @JSONField(name = "client_id")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JSONField(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    @JSONField(name = "user_id")
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @JSONField(name = "extra")
    public String getExtra() {
        return extra;
    }

    @JSONField(name = "extra")
    public void setExtra(String extra) {
        this.extra = extra;
    }

    @JSONField(name = "access_token")
    public String getAccessToken() {
        return accessToken;
    }

    @JSONField(name = "access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    public Date getExpires() {
        return expires;
    }

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}