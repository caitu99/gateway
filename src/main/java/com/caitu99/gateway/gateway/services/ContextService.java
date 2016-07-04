package com.caitu99.gateway.gateway.services;

import com.caitu99.gateway.apiconfig.model.CarmenParamMapping;
import com.caitu99.gateway.gateway.Constants;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.oauth.model.OpenOauthAccessTokens;
import com.caitu99.gateway.oauth.model.OpenOauthClients;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContextService {

    private static final List<String> tokenParams = new ArrayList<>();
    private static final List<String> appParams = new ArrayList<>();
    private static final List<String> clientParams = new ArrayList<>();
    private static final List<String> requestParams = new ArrayList<>();

    static {
        tokenParams.add(Constants.CONTEXT_ACCESS_TOKEN);
        tokenParams.add(Constants.CONTEXT_USER_ID);
        tokenParams.add(Constants.CONTEXT_CLIENT_ID);

        appParams.add(Constants.CONTEXT_USER_ID);

        clientParams.add(Constants.CONTEXT_CLIENT_NAME);
        clientParams.add(Constants.CONTEXT_CLIENT_SECRET);
        clientParams.add(Constants.CONTEXT_CLIENT_NUM);
        clientParams.add(Constants.CONTEXT_CLIENT_SOURCE);
        clientParams.add(Constants.CONTEXT_CLIENT_TYPE);

        requestParams.add(Constants.CONTEXT_REQUEST_IP);
    }

    public static void prepareContext(OpenOauthAccessTokens tokens, RequestEvent event,
                                      List<CarmenParamMapping> mappings) {
        Map<String, Object> params = event.getIntParams();
        for (CarmenParamMapping mapping : mappings) {
            if (mapping.getDataFrom() == 2) {
                // context params
                for (String p : tokenParams) {
                    if (mapping.getFieldName().equals(p)) {
                        params.put(mapping.getApiParamName(), tokens.getValue(p));
                    }
                }

                // request params
                for (String p : requestParams) {
                    if (mapping.getFieldName().equals(p)) {
                        params.put(mapping.getApiParamName(), event.getValue(Constants.CONTEXT_REQUEST_IP));
                    }
                }
            }
        }
    }

    public static void prepareContext(OpenOauthClients clients, RequestEvent event,
                                      List<CarmenParamMapping> mappings) {
        Map<String, Object> params = event.getIntParams();
        for (CarmenParamMapping mapping : mappings) {
            if (mapping.getDataFrom() == 2) {
                // context params
                for (String p : clientParams) {
                    if (mapping.getFieldName().equals(p)) {
                        params.put(mapping.getApiParamName(), clients.getValue(p));
                    }
                }
            }
        }
    }

}
