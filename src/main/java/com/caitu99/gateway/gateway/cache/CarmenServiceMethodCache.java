package com.caitu99.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.CarmenServiceMethod;
import com.caitu99.gateway.apiconfig.service.ICarmenServiceMethodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("carmenServiceMethod")
public class CarmenServiceMethodCache implements ICacheClear {

    private static Logger logger = LoggerFactory.getLogger(CarmenServiceMethodCache.class);

    @Autowired
    private ICarmenServiceMethodService serviceMethodService;

    @Autowired
    private AppConfig config;

    private Cache<String, CarmenServiceMethod> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public CarmenServiceMethod get(UUID eventId, long serviceMethodId) {
        CarmenServiceMethod carmenApi = null;
        try {
            carmenApi = cache.get(String.valueOf(serviceMethodId), () -> serviceMethodService.getById(serviceMethodId));
        } catch (Exception e) {
            logger.error("error when getting cache: {}", eventId, e);
        }
        return carmenApi;
    }

    @Override
    public void load() {
        List<Long> methodIdList = serviceMethodService.getMethodIdList(config.env);
        for (Long methodId : methodIdList) {
            get(null, methodId);
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
