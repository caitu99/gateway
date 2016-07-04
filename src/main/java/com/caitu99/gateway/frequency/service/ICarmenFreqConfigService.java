package com.caitu99.gateway.frequency.service;

import com.caitu99.gateway.frequency.model.CarmenFreqConfig;

import java.util.List;

/**
 * Created by chenyun on 15/8/10.
 */
public interface ICarmenFreqConfigService {

    final String prefix = "carmen_freq_config_";

    long insert(CarmenFreqConfig config);

    void update(CarmenFreqConfig config);

    void deleteById(long id);

    CarmenFreqConfig getById(long id);

    CarmenFreqConfig getValueByCondition(int type, long apiRef,
                                         long clientId);

    CarmenFreqConfig getValueByApiAndType(int type, long apiRef);

    List<CarmenFreqConfig> getListValueByCondition(long apiRef,
                                                   long clientId);

    List<CarmenFreqConfig> getListValueByApi(long apiRef);
}
