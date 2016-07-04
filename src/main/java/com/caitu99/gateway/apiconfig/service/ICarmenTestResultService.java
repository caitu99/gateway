package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.model.CarmenTestResult;

/**
 * Created by chenyun on 15/9/19.
 */
public interface ICarmenTestResultService {

    int insert(CarmenTestResult config);

    void update(CarmenTestResult config);

    void deleteById(int id);

    CarmenTestResult getById(int id);

    /**
     *
     * @param refApiId --开放接口主键
     * @return
     */
    CarmenTestResult getByRefApiId(long refApiId);

}
