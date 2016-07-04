package com.caitu99.gateway.oauth.service.impl;


import com.caitu99.gateway.oauth.dao.gateway.IOauthRefreshTokensDAO;
import com.caitu99.gateway.oauth.model.OpenOauthRefreshTokens;
import com.caitu99.gateway.oauth.service.IOpenOauthRefreshTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class OpenOauthRefreshTokensService extends BaseService implements IOpenOauthRefreshTokensService {

    @Autowired
    private IOauthRefreshTokensDAO openOauthRefreshTokensDAO;

    public IOauthRefreshTokensDAO getOpenOauthRefreshTokensDAO() {
        return openOauthRefreshTokensDAO;
    }

    public void setOpenOauthRefreshTokensDAO(IOauthRefreshTokensDAO openOauthRefreshTokensDAO) {
        this.openOauthRefreshTokensDAO = openOauthRefreshTokensDAO;
    }

    public OpenOauthRefreshTokens getById(Integer id) {
        return openOauthRefreshTokensDAO.getById(id);
    }

    public OpenOauthRefreshTokens getByToken(String token) {
        return openOauthRefreshTokensDAO.getByToken(token);
    }

    @Override
    public List<OpenOauthRefreshTokens> getByAccessTokens(List<String> list) {
        if (list.size() == 0) {
            return new LinkedList<>();
        }
        return openOauthRefreshTokensDAO.getByAccessTokens(list);
    }

    public void deleteById(Integer id) {
        openOauthRefreshTokensDAO.deleteById(id);
    }

    @Override
    public void deleteByToken(String token) {
        openOauthRefreshTokensDAO.deleteByToken(token);
    }

    @Override
    public void deleteByAccessTokens(List<String> list) {
        openOauthRefreshTokensDAO.deleteByAccessTokens(list);
    }

    public int save(OpenOauthRefreshTokens openOauthRefreshTokens) {
        openOauthRefreshTokensDAO.save(openOauthRefreshTokens);
        return openOauthRefreshTokens.getId();
    }

    public void update(OpenOauthRefreshTokens openOauthRefreshTokens) {
        openOauthRefreshTokensDAO.update(openOauthRefreshTokens);
    }
}
