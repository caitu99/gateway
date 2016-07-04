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
public class OAuthPasswordRequest extends OAuthRequest {

    public OAuthPasswordRequest(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException {
        super(request);
    }

    @Override
    protected OAuthValidator<HttpServletRequest> initValidator() throws OAuthProblemException, OAuthSystemException {
        Integer type = Integer.valueOf(this.request.getParameter("type"));
        return new PasswordValidatorEx(type);
    }

    public String getUserName() {
        Integer type = Integer.valueOf(this.request.getParameter("type"));
        if(type==0) {
            return getParam("mobile");
        }
        else if (type == 4) {
            return getParam("username");
        } else {
            return getParam("uid");
        }
    }

    public String getPassword() {
        Integer type = Integer.valueOf(this.request.getParameter("type"));
        if(type==0) {
            return getParam("vcode");   //验证码
        }
        else if (type==4) {
            return getParam("password");
        }
        else {
            return "";//return a empty string
        }
    }

    public String getGrantType() {
        return getParam(OAuth.OAUTH_GRANT_TYPE);
    }

    public String getNickName(){return this.request.getParameter("nickname");}
    public String getProfileimg(){return this.request.getParameter("profileimg");}
    public String getType(){return this.request.getParameter("type");}

}