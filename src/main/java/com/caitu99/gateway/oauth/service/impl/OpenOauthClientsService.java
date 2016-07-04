package com.caitu99.gateway.oauth.service.impl;


import com.caitu99.gateway.oauth.dao.gateway.IOauthClientsDAO;
import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.gateway.oauth.service.IOpenOauthClientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenOauthClientsService extends BaseService implements IOpenOauthClientsService {

    @Autowired
    private IOauthClientsDAO openOauthClientsDAO;

    public IOauthClientsDAO getOpenOauthClientsDAO() {
        return openOauthClientsDAO;
    }

    public void setOpenOauthClientsDAO(IOauthClientsDAO openOauthClientsDAO) {
        this.openOauthClientsDAO = openOauthClientsDAO;
    }

    public OpenOauthClients getById(Integer id) {
        return openOauthClientsDAO.getById(id);
    }

    public OpenOauthClients getByClientId(String clientId) {
        return openOauthClientsDAO.getByClientId(clientId);
    }

    @Override
    public OpenOauthClients getByUserId(Integer userId) {
        return openOauthClientsDAO.getByUserId(userId);
    }

    public void deleteById(Integer id) {
        openOauthClientsDAO.deleteById(id);
    }

    public void deleteByClientId(String clientId) {
        openOauthClientsDAO.deleteByClientId(clientId);
    }

    public int save(OpenOauthClients openOauthClients) {
        openOauthClientsDAO.save(openOauthClients);
        return openOauthClients.getId();
    }

    public void update(OpenOauthClients openOauthClients) {
        openOauthClientsDAO.update(openOauthClients);
    }

    @Override
    public List<OpenOauthClients> getAll() {
        return openOauthClientsDAO.getAll();
    }

    @Override
    public List<String> getClientIdList() {
        return openOauthClientsDAO.getClientIdList();
    }
}
