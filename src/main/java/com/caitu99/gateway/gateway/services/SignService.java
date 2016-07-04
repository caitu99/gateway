package com.caitu99.gateway.gateway.services;

import com.caitu99.gateway.apiconfig.model.OpenResource;
import com.caitu99.gateway.gateway.Constants;
import com.caitu99.gateway.gateway.cache.OpenOauthClientsCache;
import com.caitu99.gateway.gateway.cache.OpenResourceCache;
import com.caitu99.gateway.gateway.exception.SignValidateException;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.gateway.oauth.oauthex.OAuthConstants;
import com.caitu99.gateway.utils.AppUtils;
import com.caitu99.gateway.utils.DateUtil;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class SignService {

    private static final Logger logger = LoggerFactory.getLogger(SignService.class);

    @Autowired
    private OpenOauthClientsCache clientsCache;

    @Autowired
    private OpenResourceCache resourceCache;

    /**
     * validate sign, please see http://gateway.caitu99.com/doc/api/protocol
     */
    public OpenOauthClients validateSign(RequestEvent event) throws SignValidateException, ExecutionException {

        String clientId = event.getValue(Constants.CLIENTID);
        String sign = event.getSign();
        Map<String, String> params = event.getSignParams();
        String resource = event.getNamespace() + "." + event.getMethod();

        if (StringUtils.isEmpty(sign)) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_SIGN,
                    OAuthConstants.OAuthDescription.INVALID_NO_SIGN);
        }

        if (StringUtils.isEmpty(params.get("timestamp"))) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_TIME_STAMP,
                    OAuthConstants.OAuthDescription.INVALID_TIME_STAMP);
        }

        OpenOauthClients clients = clientsCache.get(event.getId(), clientId);
        if (clients == null) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_CLIENT,
                    OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION);
        }

        /**
         * check time stamp
         */
        String timeStamp = params.get("timestamp");
        Date time = null;
        try {
            time = DateUtil.string2Date(timeStamp, "yyyy-MM-dd HH:mm:ss");
        } catch (RuntimeException e) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_TIME_STAMP,
                    OAuthConstants.OAuthDescription.INVALID_TIME_STAMP);
        }

        if (time == null) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_TIME_STAMP,
                    OAuthConstants.OAuthDescription.INVALID_TIME_STAMP);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MINUTE, 10);

        if (Calendar.getInstance().after(calendar)) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_TIME_STAMP,
                    OAuthConstants.OAuthDescription.INVALID_TIME_STAMP);
        }

        Set<String> keys = params.keySet();
        Set<String> sortedKeys = new TreeSet<>();
        sortedKeys.addAll(keys);

        /**
         * calculate sign and check the sign from parameters
         */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(clients.getClientSecret());
        for (String s : sortedKeys) {
            stringBuilder.append(s);
            stringBuilder.append(params.get(s));
        }
        stringBuilder.append(clients.getClientSecret());
        String paramsString = stringBuilder.toString();
        String encodedSign = AppUtils.MD5(paramsString);

        if (encodedSign == null || !encodedSign.equalsIgnoreCase(sign)) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_SIGN_FOR_VALID,
                    OAuthConstants.OAuthDescription.INVALID_SIGN_FOR_VALID);
        }

        /**
         * check api resource
         */
        OpenResource openResource = resourceCache.get(event.getId(), resource);
        if (openResource == null) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_RESOURCE,
                    OAuthConstants.OAuthDescription.INVALID_RESOURCE);
        }

        /**
         * check scope info
         */
        Set<String> scopes = OAuthUtils.decodeScopes(clients.getDefaultScope());
        for (String scope : scopes) {
            if (scope.equals("all") || scope.equals(openResource.getGroupAlias())) {
                return clients;
            }
        }

        // there may be some better ways, but...
        throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_SIGN,
                OAuthConstants.OAuthDescription.INVALID_RESOURCE);
    }

}