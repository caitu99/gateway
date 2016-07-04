package com.caitu99.gateway.gateway.pipes.impl;

import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.CarmenParamMapping;
import com.caitu99.gateway.exception.CarmenException;
import com.caitu99.gateway.gateway.cache.CarmenParamMappingCache;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.model.RequestState;
import com.caitu99.gateway.gateway.model.ValidateType;
import com.caitu99.gateway.gateway.services.ContextService;
import com.caitu99.gateway.gateway.services.SignService;
import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.gateway.utils.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class SignPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(SignPipe.class);

    private SignService signService;
    private CarmenParamMappingCache paramMappingCache;
    private AppConfig appConfig;

    public SignPipe() {
        signService = SpringContext.getBean(SignService.class);
        paramMappingCache = SpringContext.getBean(CarmenParamMappingCache.class);
        appConfig = SpringContext.getBean(AppConfig.class);
    }

    @Override
    public void onEvent(RequestEvent event) {
        try {
            logger.debug("begin of sign validation: {}", event);

            // must be sign type
            if (event.getValidateType() != ValidateType.SIGN)
                return;

            // validate sign
            OpenOauthClients clients = signService.validateSign(event);

            // get param mappings
            List<CarmenParamMapping> paramMappings = paramMappingCache.get(event.getId(), event.getNamespace(),
                    event.getMethod(), event.getVersion(), appConfig.env);

            // set context parameter from OpenApp
            ContextService.prepareContext(clients, event, paramMappings);

        } catch (CarmenException e) {
            event.setException(e);
            logger.info("exception happened when validating sign: {}", event.getId(), e);
        } catch (Exception e) {
            event.setException(e);
            logger.error("exception happened when validating sign: {}", event.getId(), e);
        } finally {
            logger.info("complete sign validation: {}", event.getId());
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
