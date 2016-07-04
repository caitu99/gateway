package com.caitu99.gateway.oauth.dao.gateway;


import com.caitu99.gateway.oauth.model.OpenOauthAccessTokens;
import com.caitu99.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IOauthAccessTokensDAO extends IEntityDAO<OpenOauthAccessTokens, OpenOauthAccessTokens> {

    OpenOauthAccessTokens getByToken(String token);

    List<OpenOauthAccessTokens> getByClientIdAndUserId(@Param("clientId") String clientId, @Param("userId") int userId);

    void deleteByToken(String token);

    void deleteByIds(List<Integer> list);

}