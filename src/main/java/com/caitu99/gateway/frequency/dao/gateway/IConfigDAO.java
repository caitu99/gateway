package com.caitu99.gateway.frequency.dao.gateway;

import com.caitu99.gateway.frequency.model.CarmenConfig;
import com.caitu99.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

public interface IConfigDAO extends IEntityDAO<CarmenConfig, CarmenConfig> {

    CarmenConfig getByConfigName(@Param("configName") String configName);
}