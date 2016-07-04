package com.caitu99.gateway.oauth.oauthex;

import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;

import java.util.HashMap;
import java.util.Map;

public class OAuthASResponseEx extends OAuthASResponse {

    protected OAuthASResponseEx(String uri, int responseStatus) {
        super(uri, responseStatus);
    }

    public static OAuthErrorResponseBuilderEx errorResponse(int code) {
        return new OAuthErrorResponseBuilderEx(code);
    }

    public static class OAuthErrorResponseBuilderEx extends OAuthErrorResponseBuilder {

        public final static String error_response = "error_response";

        private Map<String, Object> extraMap = null;

        public OAuthErrorResponseBuilderEx(int responseCode) {
            super(responseCode);
            //extraMap = new HashMap<>();
            //this.parameters.put(error_response, extraMap);
        }

        public OAuthErrorResponseBuilder setError(String error) {
            this.parameters.put(OAuthError.OAUTH_ERROR, error);
            //this.extraMap.put("code", error);
            return this;
        }

        public OAuthErrorResponseBuilder setErrorDescription(String desc) {
            this.parameters.put(OAuthError.OAUTH_ERROR_DESCRIPTION, desc);
            //this.extraMap.put("msg", desc);
            return this;
        }

    }

}
