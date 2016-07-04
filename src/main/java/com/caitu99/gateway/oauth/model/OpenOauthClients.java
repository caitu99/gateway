package com.caitu99.gateway.oauth.model;

import com.caitu99.gateway.gateway.Constants;

import java.io.Serializable;

public class OpenOauthClients implements Serializable {
    private Integer id;

    private String clientId;

    private String clientSecret;

    private String clientName;

    private String redirectUri;

    private String grantTypes;

    private String scope;

    private String userId;

    private String clientType;

    private Byte clientNum;

    private String clientSource;

    private String defaultScope;

    public String getValue(String name) {
        switch (name) {
            case Constants.CONTEXT_CLIENT_NAME:
                return clientName;
            case Constants.CONTEXT_CLIENT_SECRET:
                return clientSecret;
            case Constants.CONTEXT_CLIENT_TYPE:
                return clientType;
            case Constants.CONTEXT_CLIENT_NUM:
                return String.valueOf(clientNum);
            case Constants.CONTEXT_CLIENT_SOURCE:
                return clientSource;
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(String grantTypes) {
        this.grantTypes = grantTypes;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public Byte getClientNum() {
        return clientNum;
    }

    public void setClientNum(Byte clientNum) {
        this.clientNum = clientNum;
    }

    public String getClientSource() {
        return clientSource;
    }

    public void setClientSource(String clientSource) {
        this.clientSource = clientSource;
    }

    public String getDefaultScope() {
        return defaultScope;
    }

    public void setDefaultScope(String defaultScope) {
        this.defaultScope = defaultScope;
    }

}