package com.caitu99.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.caitu99.gateway.apiconfig.model.OpenResource;
import com.caitu99.gateway.apiconfig.service.IOpenResourceService;
import com.caitu99.gateway.oauth.service.impl.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("openResource")
public class OpenResourceCache implements ICacheClear {

    private static Logger logger = LoggerFactory.getLogger(OpenResourceCache.class);

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private IOpenResourceService resourceService;

    private Cache<String, OpenResource> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public OpenResource get(UUID eventId, String resource) {
        OpenResource openResource = null;
        try {
            openResource = cache.get(resource, () -> oAuthService.getOpenResourceByUri(resource));
        } catch (Exception e) {
            logger.error("error when getting cache: {}", eventId, e);
        }
        return openResource;
    }

    @Override
    public void load() {
        List<String> resourceUriList = resourceService.getResourceUriList();
        for (String uri : resourceUriList) {
            get(null, uri);
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
