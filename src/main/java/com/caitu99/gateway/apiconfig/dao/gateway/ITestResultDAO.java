package com.caitu99.gateway.apiconfig.dao.gateway;

import com.caitu99.gateway.apiconfig.model.CarmenTestResult;
import com.caitu99.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

public interface ITestResultDAO extends IEntityDAO<CarmenTestResult,CarmenTestResult> {

   CarmenTestResult getByRefApiId(@Param("refApiId") long refApiId);
}