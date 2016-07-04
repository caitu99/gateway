package com.caitu99.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.gateway.apiconfig.service.ICarmenApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("carmenApi")
public class CarmenApiCache implements ICacheClear {

    private static Logger logger = LoggerFactory.getLogger(CarmenApiCache.class);

    @Autowired
    private ICarmenApiService apiService;

    @Autowired
    private AppConfig config;

    private Cache<String, CarmenApi> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public CarmenApi get(UUID eventId, String namespace, String methodName, String version, byte env) {
        String key = namespace + "_" + methodName + "_" + version + "_" + env;
        CarmenApi carmenApi = null;
        try {
            carmenApi = cache.get(key, () -> apiService.getRecordByCondition(namespace, methodName, version, env));
        } catch (Exception e) {
            logger.error("error when getting cache: {}", eventId, e);
        }
        return carmenApi;
    }

    @Override
    public void load() {

        List<CarmenApi> carmenApiList=apiService.getRecordByEnv(config.env);
        for (CarmenApi carmenApi : carmenApiList) {
            get(null, carmenApi.getNamespace(),carmenApi.getName()
                    ,carmenApi.getVersion(),config.env);
        }

    }

    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public long cacheSize() {
        return cache.size();
    }
}
