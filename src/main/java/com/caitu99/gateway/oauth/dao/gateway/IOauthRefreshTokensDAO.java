package com.caitu99.gateway.oauth.dao.gateway;

import com.caitu99.gateway.oauth.model.OpenOauthRefreshTokens;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface IOauthRefreshTokensDAO extends IEntityDAO<OpenOauthRefreshTokens, OpenOauthRefreshTokens> {

    OpenOauthRefreshTokens getByToken(String token);

    List<OpenOauthRefreshTokens> getByAccessTokens(List<String> list);

    void deleteByToken(String token);

    void deleteByAccessTokens(List<String> list);

}