package com.caitu99.gateway.apiconfig.service.imp;

import com.caitu99.gateway.apiconfig.dao.gateway.IAdminDAO;
import com.caitu99.gateway.apiconfig.model.CarmenUser;
import com.caitu99.gateway.apiconfig.service.ICarmenUserService;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/8/27.
 */
@Service
public class CarmenUserService implements ICarmenUserService {

    @Resource
    private IAdminDAO carmenUserDAO;

    @Override
    public int insert(CarmenUser user) {
        carmenUserDAO.save(user);
        return user.getId();
    }

    @Override
    public void update(CarmenUser user) {
        carmenUserDAO.update(user);
    }

    @Override
    public void deleteById(int id) {
        carmenUserDAO.deleteById(id);
    }

    @Override
    public CarmenUser getById(int id) {
        return carmenUserDAO.getById(id);
    }

    @Override
    public List<CarmenUser> getByUserName(String username) {
        return carmenUserDAO.getByUserName(username);
    }

    @Override
    public List<CarmenUser> getAllList() {
        return carmenUserDAO.getAllList();
    }
}
