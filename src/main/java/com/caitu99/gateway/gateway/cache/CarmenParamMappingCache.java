package com.caitu99.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.CarmenParamMapping;
import com.caitu99.gateway.apiconfig.model.CarmenParamMappingFilter;
import com.caitu99.gateway.apiconfig.service.imp.CarmenParamMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("carmenParamMapping")
public class CarmenParamMappingCache implements ICacheClear {

    private static Logger logger = LoggerFactory.getLogger(CarmenParamMappingCache.class);

    @Autowired
    private CarmenParamMappingService mappingService;

    @Autowired
    private AppConfig config;

    private Cache<String, List<CarmenParamMapping>> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public List<CarmenParamMapping> get(UUID eventId, String service, String method, String version, byte env) {
        String key = service + "_" + method + "_" + version + "_" + env;
        List<CarmenParamMapping> mappings = null;
        try {
            mappings = cache.get(key, () -> mappingService.getRecordByCondition(service, method, version, env));
        } catch (Exception e) {
            logger.error("error when getting cache", e);
        }
        return mappings;
    }

    @Override
    public void load() {
        List<CarmenParamMappingFilter> paramMappingFilter =
                mappingService.getParamMappingFilter(config.env);
        for (CarmenParamMappingFilter mappingFilter : paramMappingFilter) {
            get(null, mappingFilter.getApiNamespace(),mappingFilter.getApiName(),mappingFilter
                    .getVersion(),config.env);
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
