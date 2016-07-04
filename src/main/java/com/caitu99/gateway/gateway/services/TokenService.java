package com.caitu99.gateway.gateway.services;

import com.caitu99.gateway.apiconfig.model.OpenResource;
import com.caitu99.gateway.gateway.Constants;
import com.caitu99.gateway.gateway.cache.OpenOauthClientsCache;
import com.caitu99.gateway.gateway.cache.OpenResourceCache;
import com.caitu99.gateway.gateway.exception.TokenValidateException;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.oauth.model.OpenOauthAccessTokens;
import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.gateway.oauth.oauthex.OAuthConstants;
import com.caitu99.gateway.oauth.service.impl.BaseService;
import com.caitu99.gateway.oauth.service.impl.OAuthService;
import com.caitu99.gateway.utils.AppUtils;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class TokenService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private OpenOauthClientsCache clientsCache;

    @Autowired
    private OpenResourceCache resourceCache;

    /**
     * validate access token, please see http://gateway.caitu99.com/doc/api/oauthprotocol
     */
    public OpenOauthAccessTokens validateAccessToken(RequestEvent event) throws TokenValidateException, ExecutionException {

        String accessToken = event.getAccessToken();
        String resource = event.getNamespace() + "." + event.getMethod();

        if (StringUtils.isEmpty(accessToken)) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_TOKEN,
                    OAuthConstants.OAuthDescription.INVALID_TOKEN);
        }

        if (StringUtils.isEmpty(resource)) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_RESOURCE,
                    OAuthConstants.OAuthDescription.INVALID_RESOURCE);
        }

        OpenOauthAccessTokens openOauthAccessTokens;
        try {
            event.getTicTac().tic(Constants.ST_REDIS_TOKEN);
            openOauthAccessTokens = oAuthService.getAccessToken(accessToken);
        } finally {
            event.getTicTac().tac(Constants.ST_REDIS_TOKEN);
        }
        if (openOauthAccessTokens == null) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_OR_EXPIRED_TOKEN,
                    OAuthConstants.OAuthDescription.INVALID_TOKEN);
        }

        /**
         * check if expired
         */
        if (!AppUtils.checkBefore(openOauthAccessTokens.getExpires())) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_OR_EXPIRED_TOKEN,
                    OAuthConstants.OAuthDescription.INVALID_TOKEN_EXPIRED);
        }

        String clientId = openOauthAccessTokens.getClientId();
        OpenOauthClients openOauthClients = clientsCache.get(event.getId(), clientId);
        if (openOauthClients == null) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.INVALID_CLIENT_NO,
                    OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION);
        }

        /**
         * check api resource
         */
        OpenResource openResource = resourceCache.get(event.getId(), resource);
        if (openResource == null) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.INVALID_RESOURCE,
                    OAuthConstants.OAuthDescription.INVALID_RESOURCE);
        }

        /**
         * check scope info, if it has 'all' in the token then return immediately
         */
        Set<String> scopes = OAuthUtils.decodeScopes(openOauthAccessTokens.getScope());
        for (String scope : scopes) {
            if (scope.equals("all") || scope.equals(openResource.getGroupAlias())) {
                return openOauthAccessTokens;
            }
        }

        // there may be some better ways, but...
        throw new TokenValidateException(OAuthConstants.OAuthResponse.INVALID_TOKEN,
                OAuthConstants.OAuthDescription.INVALID_RESOURCE);
    }

}