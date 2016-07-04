package com.caitu99.gateway.oauth.controller;

import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.oauth.Constants;
import com.caitu99.gateway.oauth.model.OAuthAuthzParameters;
import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.gateway.oauth.oauthex.OAuthASResponseEx;
import com.caitu99.gateway.oauth.oauthex.OAuthConstants;
import com.caitu99.gateway.oauth.oauthex.UUIDValueGeneratorEx;
import com.caitu99.gateway.oauth.service.impl.OAuthService;
import com.caitu99.gateway.utils.AppUtils;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

@SuppressWarnings("unchecked")
@Controller
public class AuthorizeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizeController.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private OAuthService oAuthService;

    /**
     * used for authorization code type and implicit type which are the
     * authorization type of oauth, please see: http://gateway.caitu99.com/doc/oauth
     *  1. the authorization code with store in redis for 2 minutes, so
     *     the client should get the code as soon as possible.
     * @param model
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws OAuthSystemException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/oauth/authorize", method = RequestMethod.GET, produces="application/json;charset=utf-8")
    public Object authorize(Model model, HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException, UnsupportedEncodingException {
        try {
            HttpSession session = request.getSession();

            // request
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            OAuthAuthzParameters oAuthAuthzParameters = new OAuthAuthzParameters(oauthRequest);
            String responseType = oAuthAuthzParameters.getResponseType();

            // check client id
            OpenOauthClients openOauthClients = oAuthService.getClientByClientId(oAuthAuthzParameters.getClientId());
            if (openOauthClients == null) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_NO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                logger.info("can not get client, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            if (responseType.equals(ResponseType.CODE.toString())
                    && !openOauthClients.getGrantTypes().contains(Constants.OAUTH_AUTHORIZATION_CODE)) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                        .buildJSONMessage();
                logger.info("no grant type, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            } else if (responseType.equals(ResponseType.TOKEN.toString())
                    && !openOauthClients.getGrantTypes().contains(Constants.OAUTH_IMPLICIT)) {
                OAuthResponse response = OAuthASResponseEx
                        .errorResponse(HttpServletResponse.SC_OK)
                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_CLIENT_INFO))
                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_GRANT_TYPE)
                        .buildJSONMessage();
                logger.info("no grant type, context: {}", oAuthAuthzParameters);
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // need login
            //if (session.getAttribute(Constants.USER_ID) == null || session.getAttribute(Constants.KDT_ID) == null) {
            if (session.getAttribute(Constants.USER_ID) == null) {
                // set some sessions
                session.setAttribute(Constants.CLIENT_ID, openOauthClients.getClientId());
                session.setAttribute(Constants.CLIENT_NAME, openOauthClients.getClientName());
                session.setAttribute(Constants.CLIENT_QUERY, URLEncoder.encode(request.getQueryString(), Constants.APP_ENCODING));
                logger.info("session does not exist, context: {}", oAuthAuthzParameters);
                return new ModelAndView("redirect:/oauth/login?" + session.getAttribute(Constants.CLIENT_QUERY));
            }

            oAuthAuthzParameters.setUserId((Integer) session.getAttribute(Constants.USER_ID));
            //oAuthAuthzParameters.setKdtId((Integer) session.getAttribute(Constants.KDT_ID));
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

            // check the top domain
            String redirectUri = openOauthClients.getRedirectUri();
            String[] urlArr = redirectUri.split(" ");
            boolean flag = false;
            for (String url : urlArr) {
                if (oAuthAuthzParameters.getRedirectUri() != null && url != null) {
                    String url1 = AppUtils.getTopDomain(url);
                    String url2 = AppUtils.getTopDomain(oAuthAuthzParameters.getRedirectUri());
                    if (!StringUtils.isEmpty(url1) && !StringUtils.isEmpty(url2) && url1.equals(url2)) {
                        flag = true;
                        break;
                    }
                }
            }
//            if (!flag) {
//                OAuthResponse response = OAuthASResponseEx
//                        .errorResponse(HttpServletResponse.SC_OK)
//                        .setError(String.valueOf(OAuthConstants.OAuthResponse.INVALID_REQUEST))
//                        .setErrorDescription(OAuthConstants.OAuthDescription.INVALID_REDIRECT_URI)
//                        .buildJSONMessage();
//                logger.info("top domain does not conform with each other: {} and {}", oAuthAuthzParameters.getRedirectUri(), redirectUri);
//                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
//            }

            // clear some sessions
            //session.setAttribute(Constants.KDT_ID, null);

            // build response
            OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new UUIDValueGeneratorEx());
            if (responseType.equals(ResponseType.CODE.toString())) {
                // generate authorization code
                String authorizationCode = oauthIssuerImpl.authorizationCode();
                oAuthAuthzParameters.setAuthorizeCode(authorizationCode);

                oAuthService.putOAuthAuthzParameters(authorizationCode, oAuthAuthzParameters);

                OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                        OAuthASResponseEx.authorizationResponse(request, HttpServletResponse.SC_FOUND);
                builder.setCode(authorizationCode);
                builder.location(oAuthAuthzParameters.getRedirectUri());

                OAuthResponse response = builder.buildQueryMessage();
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(new URI(response.getLocationUri()));
                logger.info("response authorization token: {}, context: {}", authorizationCode, oAuthAuthzParameters);
                return new ResponseEntity("", headers, HttpStatus.valueOf(response.getResponseStatus()));
            } else {
                String accessToken = oauthIssuerImpl.accessToken();

                /**
                 * limit client access token
                  */
                int limitNum = openOauthClients.getClientNum();
                limitNum = limitNum > 0 ? limitNum - 1 : limitNum;
                oAuthService.limitAccessToken(oAuthAuthzParameters.getClientId(),
                        oAuthAuthzParameters.getUserId(), limitNum);

                oAuthService.saveAccessToken(accessToken, oAuthAuthzParameters);

                OAuthASResponse.OAuthTokenResponseBuilder builder = OAuthASResponseEx.tokenResponse(302);
                builder.setTokenType("uuid");
                builder.setAccessToken(accessToken);
                builder.setExpiresIn(String.valueOf(appConfig.tokenAccessExpire));
                builder.setScope(oAuthAuthzParameters.getScope());
                builder.location(oAuthAuthzParameters.getRedirectUri());

                OAuthResponse response = builder.buildQueryMessage();
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(new URI(response.getLocationUri()));
                logger.info("response access token: {}, context: {}", accessToken, oAuthAuthzParameters);
                return new ResponseEntity("", headers, HttpStatus.valueOf(response.getResponseStatus()));
            }
        } catch (OAuthProblemException e) {
            String redirectUri = e.getRedirectUri();
            if (OAuthUtils.isEmpty(redirectUri)) {
                logger.warn("unknown redirect uri", e);
                return new ResponseEntity<String>(OAuthConstants.OAuthDescription.INVALID_REDIRECT_URI_NOT_FOUND,
                        HttpStatus.OK);
            }

            OAuthResponse response = OAuthASResponseEx
                    .errorResponse(HttpServletResponse.SC_OK)
                    .error(e)
                    .location(redirectUri)
                    .buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            logger.error("response error", e);
            return new ResponseEntity("", headers, HttpStatus.valueOf(response.getResponseStatus()));
        }
    }

}