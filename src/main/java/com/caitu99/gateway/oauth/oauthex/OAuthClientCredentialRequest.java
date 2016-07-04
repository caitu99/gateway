package com.caitu99.gateway.oauth.oauthex;

import org.apache.oltu.oauth2.as.request.OAuthRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.validators.OAuthValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bobo on 6/8/15.
 */
public class OAuthClientCredentialRequest extends OAuthRequest {

    public OAuthClientCredentialRequest(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException {
        super(request);
    }

    @Override
    protected OAuthValidator<HttpServletRequest> initValidator() throws OAuthProblemException, OAuthSystemException {
        return new ClientCredentialValidatorEx();
    }

    public String getGrantType() {
        return getParam(OAuth.OAUTH_GRANT_TYPE);
    }

}