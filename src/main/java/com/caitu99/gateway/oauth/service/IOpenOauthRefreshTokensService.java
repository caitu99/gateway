package com.caitu99.gateway.oauth.service;


import com.caitu99.gateway.oauth.model.OpenOauthRefreshTokens;

import java.util.List;

public interface IOpenOauthRefreshTokensService {

    OpenOauthRefreshTokens getById(Integer id);

    OpenOauthRefreshTokens getByToken(String token);

    List<OpenOauthRefreshTokens> getByAccessTokens(List<String> list);

    void deleteById(Integer id);

    void deleteByToken(String token);

    void deleteByAccessTokens(List<String> list);

    int save(OpenOauthRefreshTokens openOauthRefreshTokens);

    void update(OpenOauthRefreshTokens openOauthRefreshTokens);

}
