package com.caitu99.gateway.apiconfig.service.imp;


import com.caitu99.gateway.apiconfig.dao.gateway.ITestResultDAO;
import com.caitu99.gateway.apiconfig.model.CarmenTestResult;
import com.caitu99.gateway.apiconfig.service.ICarmenTestResultService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/9/19.
 */
@Service
public class CarmenTestResultService implements ICarmenTestResultService {

    @Resource
    private ITestResultDAO testResultDAO;

    @Override
    public int insert(CarmenTestResult config) {
        testResultDAO.save(config);
        return config.getId();
    }

    @Override
    public void update(CarmenTestResult config) {
       testResultDAO.update(config);
    }

    @Override
    public void deleteById(int id) {
         testResultDAO.deleteById(id);
    }

    @Override
    public CarmenTestResult getById(int id) {
        return testResultDAO.getById(id);
    }

    @Override
    public CarmenTestResult getByRefApiId(long refApiId) {
        return testResultDAO.getByRefApiId(refApiId);
    }
}
