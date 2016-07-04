package com.caitu99.gateway.oauth.service;


import com.caitu99.gateway.oauth.model.OpenOauthAccessTokens;

import java.util.List;

public interface IOpenOauthAccessTokensService {

    OpenOauthAccessTokens getById(Integer id);

    OpenOauthAccessTokens getByToken(String token);

    List<OpenOauthAccessTokens> getByClientIdAndUserId(String clientId, int adminId);

    void deleteById(Integer id);

    void deleteByIds(List<Integer> list);

    void deleteByToken(String token);

    int save(OpenOauthAccessTokens openOauthAccessTokens);

    void update(OpenOauthAccessTokens openOauthAccessTokens);

}
