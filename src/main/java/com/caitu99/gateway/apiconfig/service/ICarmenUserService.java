package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.model.CarmenUser;

import java.util.List;

/**
 * Created by chenyun on 15/8/27.
 */
public interface ICarmenUserService {

    final String prefix = "carmen_user_";

    int insert(CarmenUser user);

    void update(CarmenUser user);

    void deleteById(int id);

    CarmenUser getById(int id);

    List<CarmenUser> getByUserName(String username);

    List<CarmenUser> getAllList();

}
