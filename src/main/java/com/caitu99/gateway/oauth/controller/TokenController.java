package com.caitu99.gateway.oauth.controller;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.oauth.Constants;
import com.caitu99.gateway.oauth.LoginType;
import com.caitu99.gateway.oauth.exception.OAuthException;
import com.caitu99.gateway.oauth.model.OAuthAuthzParameters;
import com.caitu99.gateway.oauth.model.OauthUser;
import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.gateway.oauth.model.OpenOauthRefreshTokens;
import com.caitu99.gateway.oauth.oauthex.*;
import com.caitu99.gateway.oauth.service.impl.OAuthService;
import com.caitu99.gateway.utils.AppUtils;
import com.caitu99.gateway.utils.LoginType2EnumUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;


@Controller
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private AppConfig appConfig;

    /**
     * dispatch the request according grant_type
     *
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws OAuthSystemException
     * @throws OAuthProblemException
     */
    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Object entry(HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException, OAuthProblemException {

        String type = request.getParameter("grant_type");

        if (StringUtils.isNotEmpty(type)) {
            switch (type) {
                case "authorization_code":
                    return this.token(request);
                case "refresh_token":
                    return this.refresh(request);
                case "client_credentials":
                    return this.credential(request);
                case "password":
                    return this.password(request);
                default:
                    ;
            }
        }

        OAuthResponse response = OAuthASResponseEx
                .errorResponse(HttpServletResponse.SC_OK)
                .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
                .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                .buildJSONMessage();
        logger.info("invalid grant type: {}", type);
        return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
    }

    /**
     * get access token for mobile login, this entry is internal.
     *
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws OAuthSystemException
     * @throws OAuthProblemException
     */
    @RequestMapping(value = "/gw/oauth/token/{version}/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Object entry_token(HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException, OAuthProblemException {
        return this.password(request);
    }

    /**
     * refresh access token for mobile login, this entry is internal.
     *
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws OAuthSystemException
     * @throws OAuthProblemException
     */
    @RequestMapping(value = "/gw/oauth/token/{version}/refresh", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Object entry2(HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException, OAuthProblemException {
        return this.refresh(request);
    }

    /**
     * get access token by authorization code
     *
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws OAuthSystemException
     */
    public Object token(HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException {
        try {
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
            String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);

            // get from cache
            OAuthAuthzParameters oAuthAuthzParameters = oAuthService.getOAuthAuthzParameters(authCode);
            if (oAuthAuthzParameters == null) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CODE))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_AUTHORIZATION_CODE)
                        .buildJSONMessage();
                logger.info("no cache for {}", authCode);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            if (!oAuthAuthzParameters.getClientId().equals(oauthRequest.getClientId())) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("invalid client id {}, context: {}", oauthRequest.getClientId(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // check client id
            OpenOauthClients openOauthClients = oAuthService.getClientByClientId(oauthRequest.getClientId());
            if (openOauthClients == null) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("can not get client, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            if (!openOauthClients.getGrantTypes().contains(Constants.OAUTH_AUTHORIZATION_CODE)) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                        .buildJSONMessage();
                logger.info("no grant type, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // check client secret
            if (!openOauthClients.getClientSecret().equals(oauthRequest.getClientSecret())) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("invalid secret: {}, context: {}", openOauthClients.getClientSecret(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // check redirect url
            if (!oAuthAuthzParameters.getRedirectUri().equals(oauthRequest.getRedirectURI())) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_REDIRECT_URI)
                        .buildJSONMessage();
                logger.info("invalid redirect url {}, context: {}", oauthRequest.getRedirectURI(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // build response
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new UUIDValueGeneratorEx());
            String accessToken = oauthIssuerImpl.accessToken();
            String refreshToken = oauthIssuerImpl.refreshToken();

            /**
             * limit client access token number
             */
            int limitNum = openOauthClients.getClientNum();
            limitNum = limitNum > 0 ? limitNum - 1 : limitNum;
            oAuthService.limitAccessToken(oAuthAuthzParameters.getClientId(),
                    oAuthAuthzParameters.getUserId(), limitNum);

            oAuthService.saveAccessToken(accessToken, oAuthAuthzParameters);
            oAuthService.saveRefreshToken(refreshToken, accessToken, oAuthAuthzParameters);

            OAuthResponse response = OAuthASResponseEx
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setTokenType("uuid")
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken)
                    .setExpiresIn(String.valueOf(appConfig.tokenAccessExpire))
                    .setScope(oAuthAuthzParameters.getScope())
                    .buildJSONMessage();

            //remove the code from cache
            oAuthService.delOAuthAuthzParameters(authCode);

            logger.info("response access toke {}, context: {}", accessToken, oAuthAuthzParameters);
            return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            logger.error("oauth problem exception", e);
            OAuthResponse res = OAuthASResponseEx
                    .errorResponse(HttpServletResponse.SC_OK)
                    .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
                    .setErrorDescription(e.getMessage())
                    .buildJSONMessage();
            return new ResponseEntity<String>(res.getBody(), HttpStatus.valueOf(res.getResponseStatus()));
        }

    }

    /**
     * get a new access token by refresh token
     *
     * @param request
     * @return
     * @throws OAuthProblemException
     * @throws OAuthSystemException
     */
    public Object refresh(HttpServletRequest request)
            throws OAuthProblemException, OAuthSystemException {
        try {

            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
            OAuthAuthzParameters oAuthAuthzParameters = new OAuthAuthzParameters(oauthRequest);
            String refreshToken = oauthRequest.getRefreshToken();

            if (!oAuthService.checkRefreshFrequency(refreshToken)) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_FREQUENCY))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_FRESH_FREQUENCY)
                        .buildJSONMessage();
                logger.warn("refresh token {} too frequent, context: {}", refreshToken, oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // check token
            OpenOauthRefreshTokens token = oAuthService.getRefreshToken(refreshToken);
            if (token == null) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_FRESH_TOKEN)
                        .buildJSONMessage();
                logger.warn("invalid refresh token {}, context: {}", refreshToken, oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // check the client id and the token client id
            if (!token.getClientId().equals(oauthRequest.getClientId())) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("invalid client id {} and {}, context: {}", token.getClientId(), oauthRequest.getClientId(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // check client id
            OpenOauthClients openOauthClients = oAuthService.getClientByClientId(oauthRequest.getClientId());
            if (openOauthClients == null) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("can not get client, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            if (!openOauthClients.getGrantTypes().contains(Constants.OAUTH_REFRESH_TOKEN)) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                        .buildJSONMessage();
                logger.info("no grant type, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // check client secret
            if (!openOauthClients.getClientSecret().equals(oauthRequest.getClientSecret())) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("invalid secret: {}, context: {}", openOauthClients.getClientSecret(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // check token time
            if (!AppUtils.checkBefore(token.getExpires())) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.NO_OR_EXPIRED_TOKEN))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_FRESH_TOKEN)
                        .buildJSONMessage();
                logger.info("token is expired: {}, context: {}", token.getId(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            OAuthIssuerImpl oAuthIssuer = new OAuthIssuerImpl(new UUIDValueGeneratorEx());
            String accessToken = oAuthIssuer.accessToken();

            oAuthAuthzParameters.setUserId(token.getUserId());

            if (StringUtils.isEmpty(oAuthAuthzParameters.getScope())) {
                oAuthAuthzParameters.setScope(openOauthClients.getDefaultScope());
            } else {
                oAuthAuthzParameters.setScope(
                        OAuthUtils.encodeScopes(
                                oAuthService.getRetainScopes(
                                        openOauthClients.getDefaultScope(),
                                        oAuthAuthzParameters.getScope()
                                )
                        )
                );
            }

            /**
             * limit client access token number
             */
            int limitNum = openOauthClients.getClientNum();
            limitNum = limitNum > 0 ? limitNum - 1 : limitNum;
            oAuthService.limitAccessToken(oAuthAuthzParameters.getClientId(),
                    oAuthAuthzParameters.getUserId(), limitNum);

            oAuthService.saveAccessToken(accessToken, oAuthAuthzParameters);

            OAuthResponse response = OAuthASResponseEx
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setTokenType("uuid")
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken)
                    .setExpiresIn(String.valueOf(appConfig.tokenAccessExpire))
                    .setScope(oAuthAuthzParameters.getScope())
                    .buildJSONMessage();
            logger.info("response access token: {}, context: {}", accessToken, oAuthAuthzParameters);
            return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            logger.error("oauth problem exception", e);
            OAuthResponse res = OAuthASResponseEx
                    .errorResponse(HttpServletResponse.SC_OK)
                    .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
                    .setErrorDescription(e.getMessage())
                    .buildJSONMessage();
            return new ResponseEntity<String>(res.getBody(), HttpStatus.valueOf(res.getResponseStatus()));
        }
    }

    /**
     * credential type of oauth,
     * please see: http://gateway.caitu99.com/doc/oauth
     *
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws OAuthSystemException
     */
    public Object credential(HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException {
        try {
            OAuthClientCredentialRequest oauthRequest = new OAuthClientCredentialRequest(request);
            OAuthAuthzParameters oAuthAuthzParameters = new OAuthAuthzParameters(oauthRequest);

            if (!oauthRequest.getGrantType().equals(Constants.OAUTH_CLIENT_CREDENTIALS)) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                        .buildJSONMessage();
                logger.info("invalid grant type: {}, context: {}", oauthRequest.getGrantType(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            OpenOauthClients openOauthClients = oAuthService.getClientByClientId(oAuthAuthzParameters.getClientId());
            if (openOauthClients == null) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("can not get client, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            if (!openOauthClients.getGrantTypes().contains(Constants.OAUTH_CLIENT_CREDENTIALS)) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                        .buildJSONMessage();
                logger.info("no grant type, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            if (!openOauthClients.getClientSecret().equals(oAuthAuthzParameters.getClientSecret())) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("invalid secret: {}, context: {}", openOauthClients.getClientSecret(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            if (StringUtils.isEmpty(oAuthAuthzParameters.getScope())) {
                //oAuthAuthzParameters.setScope(openOauthClients.getDefaultScope());
                oAuthAuthzParameters.setScope(appConfig.clientCredentialScope); //for security
            } else {
                oAuthAuthzParameters.setScope(
                        OAuthUtils.encodeScopes(
                                oAuthService.getRetainScopes(
                                        //openOauthClients.getDefaultScope(),
                                        appConfig.clientCredentialScope, // for security
                                        oAuthAuthzParameters.getScope()
                                )
                        )
                );
            }

            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new UUIDValueGeneratorEx());
            String accessToken = oauthIssuerImpl.accessToken();

            oAuthService.saveAccessToken(accessToken, oAuthAuthzParameters);

            OAuthResponse response = OAuthASResponseEx
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setTokenType("uuid")
                    .setAccessToken(accessToken)
                    .setExpiresIn(String.valueOf(appConfig.tokenAccessExpire))
                    .setScope(oAuthAuthzParameters.getScope())
                    .buildJSONMessage();
            logger.info("response access token {}, context: {}", accessToken, oAuthAuthzParameters);
            return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            logger.error("oauth problem exception", e);
            final OAuthResponse response = OAuthASResponseEx
                    .errorResponse(HttpServletResponse.SC_OK)
                    .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
                    .setErrorDescription(e.getMessage())
                    .buildJSONMessage();
            HttpHeaders headers = new HttpHeaders();
            return new ResponseEntity<String>(response.getBody(), headers, HttpStatus.valueOf(response.getResponseStatus()));
        }
    }

    /**
     * password type of oauth,
     * please see http://gateway.caitu99.com/doc/oauth
     *
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws OAuthSystemException
     */
    public Object password(HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException {
        try {
            String type = request.getParameter("type");
            LoginType loginType = LoginType2EnumUtils.getLoginTypeByStr(type);

            if (loginType == null) //登录类型不正确
            {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_TYPE))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                        .buildJSONMessage();
                logger.info("invalid type, context: {}", type);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            OAuthPasswordRequest oauthRequest = new OAuthPasswordRequest(request);
            OAuthAuthzParameters oAuthAuthzParameters = new OAuthAuthzParameters(oauthRequest);

            //验证GRANT_TYPE
            if (!oauthRequest.getGrantType().equals(OAuth.OAUTH_PASSWORD)) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                        .buildJSONMessage();
                logger.info("invalid grant type: {}, context: {}", oauthRequest.getGrantType(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }


            //请求频率
            if (!oAuthService.checkLoginFrequency(oAuthAuthzParameters.getUserName())) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_FREQUENCY))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_LOGIN_FREQUENCY)
                        .buildJSONMessage();
                logger.warn("login {} too frequent, context: {}", oAuthAuthzParameters.getUserName(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            //判断ClientId是否正确
            OpenOauthClients openOauthClients = oAuthService.getClientByClientId(oAuthAuthzParameters.getClientId());
            if (openOauthClients == null) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("can not get client, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            //
            if (!openOauthClients.getGrantTypes().contains(Constants.OAUTH_PASSWORD)) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                        .buildJSONMessage();
                logger.info("no grant type, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            //验证ClientSecret
            if (!openOauthClients.getClientSecret().equals(oAuthAuthzParameters.getClientSecret())) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("invalid secret: {}, context: {}", openOauthClients.getClientSecret(), oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }


            OauthUser user = null;
            try {
//                user = oAuthService.login(oAuthAuthzParameters.getUserName(),oAuthAuthzParameters.getPassword(), Integer.valueOf(type));
                user = oAuthService.login(oAuthAuthzParameters, request);
            } catch (Exception e) {
                logger.info("login error", e);
                Integer statusCode = OAuthConstants.OAuthResponse.INVALID_REQUEST;
                if (e instanceof OAuthException) {
                    statusCode = ((OAuthException) e).getCode();
                }
                final OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(statusCode))
                        .setErrorDescription(e.getMessage())
                        .buildJSONMessage();
                HttpHeaders headers = new HttpHeaders();
                return new ResponseEntity<String>(response.getBody(), headers, HttpStatus.valueOf(response.getResponseStatus()));
            }

            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new UUIDValueGeneratorEx());
            String accessToken = oauthIssuerImpl.accessToken();
            String refreshToken = oauthIssuerImpl.refreshToken();

            oAuthAuthzParameters.setUserId(user.getId());
            if (StringUtils.isEmpty(oAuthAuthzParameters.getScope())) {
                oAuthAuthzParameters.setScope(openOauthClients.getDefaultScope());
            } else {
                oAuthAuthzParameters.setScope(
                        OAuthUtils.encodeScopes(
                                oAuthService.getRetainScopes(
                                        openOauthClients.getDefaultScope(),
                                        oAuthAuthzParameters.getScope()
                                )
                        )
                );
            }

            /**
             * limit client access token number
             */
            int limitNum = openOauthClients.getClientNum();
            limitNum = limitNum > 0 ? limitNum - 1 : limitNum;
            oAuthService.limitAccessToken(oAuthAuthzParameters.getClientId(),
                    oAuthAuthzParameters.getUserId(), limitNum);

            oAuthService.saveAccessToken(accessToken, oAuthAuthzParameters);
            oAuthService.saveRefreshToken(refreshToken, accessToken, oAuthAuthzParameters);

            OAuthResponse response = OAuthASResponseEx
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setTokenType("uuid")
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken)
                    .setExpiresIn(String.valueOf(appConfig.tokenAccessExpire))
                    .setScope(oAuthAuthzParameters.getScope())
                    .buildJSONMessage();
            logger.info("response access token: {}, context: {}", accessToken, oAuthAuthzParameters);
            return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            logger.info("oauth problem exception", e);
            final OAuthResponse response = OAuthASResponseEx
                    .errorResponse(HttpServletResponse.SC_OK)
                    .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
                    .setErrorDescription(e.getMessage())
                    .buildJSONMessage();
            HttpHeaders headers = new HttpHeaders();
            return new ResponseEntity<String>(response.getBody(), headers, HttpStatus.valueOf(response.getResponseStatus()));
        }
    }

    @ExceptionHandler(Exception.class)
    public Object exception(Exception e, HttpServletRequest request) {
        JSONObject error = new JSONObject();
        error.put("code", "-1");
        error.put("message", e.toString());
        return new ResponseEntity<String>(error.toJSONString(), HttpStatus.valueOf(200));
    }

}