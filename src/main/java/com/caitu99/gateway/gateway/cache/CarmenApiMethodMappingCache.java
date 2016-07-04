package com.caitu99.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.CarmenApiMethodMapping;
import com.caitu99.gateway.apiconfig.service.ICarmenApiMethodMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("carmenApiMethodMapping")
public class CarmenApiMethodMappingCache implements ICacheClear {

    private static Logger logger = LoggerFactory.getLogger(CarmenApiMethodMappingCache.class);

    @Autowired
    private ICarmenApiMethodMappingService apiMethodMappingService;

    @Autowired
    private AppConfig config;

    private Cache<String, CarmenApiMethodMapping> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public CarmenApiMethodMapping get(UUID eventId, long apiId, byte env) {
        CarmenApiMethodMapping carmenApiMethodMapping = null;
        try {
            carmenApiMethodMapping = cache.get(String.valueOf(apiId), () -> apiMethodMappingService.getByApiId(apiId, env));
        } catch (Exception e) {
            logger.error("error when getting cache: {}", eventId, e);
        }
        return carmenApiMethodMapping;
    }

    @Override
    public void load() {
        List<Long> apiIdList = apiMethodMappingService.getApiIdList(config.env);
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
