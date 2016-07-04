package com.caitu99.gateway.gateway.pipes.impl;

import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.CarmenParamMapping;
import com.caitu99.gateway.exception.CarmenException;
import com.caitu99.gateway.gateway.cache.CarmenParamMappingCache;
import com.caitu99.gateway.gateway.cache.OpenOauthClientsCache;
import com.caitu99.gateway.gateway.exception.PipelineException;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.model.RequestState;
import com.caitu99.gateway.gateway.model.ValidateType;
import com.caitu99.gateway.gateway.services.ContextService;
import com.caitu99.gateway.gateway.services.TokenService;
import com.caitu99.gateway.oauth.model.OpenOauthAccessTokens;
import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.gateway.utils.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class TokenPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(TokenPipe.class);

    private TokenService tokenService;
    private AppConfig appConfig;

    private OpenOauthClientsCache clientsCache;
    private CarmenParamMappingCache paramMappingCache;

    public TokenPipe() {
        tokenService = SpringContext.getBean(TokenService.class);
        clientsCache = SpringContext.getBean(OpenOauthClientsCache.class);
        paramMappingCache = SpringContext.getBean(CarmenParamMappingCache.class);
        appConfig = SpringContext.getBean(AppConfig.class);
    }

    @Override
    public void onEvent(RequestEvent event) {
        try {
            logger.debug("begin of token validation: {}", event);

            // must be token type
            if (event.getValidateType() != ValidateType.TOKEN)
                return;

            // check access token
            OpenOauthAccessTokens openOauthAccessTokens = tokenService.validateAccessToken(event);

            // get client
            OpenOauthClients openOauthClients = clientsCache.get(event.getId(), openOauthAccessTokens.getClientId());

            if (openOauthClients == null) {
                throw new PipelineException(5017, "client is null");
            }

            // used for frequency control
            event.setClientId(openOauthClients.getId());
            event.setClientName(openOauthClients.getClientId());

            // get param mappings
            List<CarmenParamMapping> paramMappings = paramMappingCache.get(event.getId(), event.getNamespace(),
                    event.getMethod(), event.getVersion(), appConfig.env);

            // set context parameter from access token
            ContextService.prepareContext(openOauthAccessTokens, event, paramMappings);

            // set context parameter from OpenOauthClients
            ContextService.prepareContext(openOauthClients, event, paramMappings);

        } catch (CarmenException e) {
            event.setException(e);
            logger.info("exception happened when validating token: {}", event.getId(), e);
        } catch (Exception e) {
            event.setException(e);
            logger.error("exception happened when validating token: {}", event.getId(), e);
        }
        finally {
            logger.info("complete token validation: {}", event.getId());
            onNext(event);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    protected void onNext(RequestEvent event) {
        if (event.getException() != null)
            event.setState(RequestState.ERROR);
        else
            event.setState(RequestState.DISPATCH);

        // go on
        Pipeline.getInstance().process(event);
    }
}
