package com.caitu99.gateway.oauth.dao.gateway;


import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface IOauthClientsDAO extends IEntityDAO<OpenOauthClients, OpenOauthClients> {

    OpenOauthClients getByClientId(String clientId);

    void deleteByClientId(String clientId);

    OpenOauthClients getByUserId(Integer userId);

    List<OpenOauthClients> getAll();

    List<String> getClientIdList();
}