package com.caitu99.gateway.gateway.cache;

import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.CarmenApiParam;
import com.caitu99.gateway.apiconfig.service.ICarmenApiParamService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("carmenApiParam")
public class CarmenApiParamCache implements ICacheClear {

    private static Logger logger = LoggerFactory.getLogger(CarmenApiParamCache.class);

    @Autowired
    private ICarmenApiParamService apiParamService;

    @Autowired
    private AppConfig config;

    private Cache<String, List<CarmenApiParam>> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public List<CarmenApiParam> get(UUID eventId, long apiId, byte env) {
        List<CarmenApiParam> carmenApiParamList = null;
        try {
            carmenApiParamList = cache.get(String.valueOf(apiId), () -> apiParamService.getByApiId(apiId, env));
        } catch (Exception e) {
            logger.error("error when getting cache: {}", eventId, e);
        }
        return carmenApiParamList;
    }

    @Override
    public void load() {

        List<Long> apiIdList = apiParamService.getApiIdList(config.env);
        for (Long apiId : apiIdList) {
            get(null, apiId,config.env);
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
