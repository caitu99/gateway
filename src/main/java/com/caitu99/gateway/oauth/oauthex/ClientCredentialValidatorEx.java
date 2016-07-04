package com.caitu99.gateway.oauth.oauthex;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.validators.AbstractValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bobo on 6/8/15.
 */
public class ClientCredentialValidatorEx extends AbstractValidator<HttpServletRequest> {

    public ClientCredentialValidatorEx() {
        requiredParams.add(OAuth.OAUTH_GRANT_TYPE);
        requiredParams.add("client_id");
        requiredParams.add("client_secret");
    }

    @Override
    public void validateMethod(HttpServletRequest request) throws OAuthProblemException {
        String method = request.getMethod();
        if (!OAuth.HttpMethod.GET.equals(method) && !OAuth.HttpMethod.POST.equals(method)) {
            throw OAuthProblemException.error(OAuthError.CodeResponse.INVALID_REQUEST)
                    .description("Method not correct.");
        }
    }

    @Override
    public void validateContentType(HttpServletRequest request) throws OAuthProblemException {
    }

}