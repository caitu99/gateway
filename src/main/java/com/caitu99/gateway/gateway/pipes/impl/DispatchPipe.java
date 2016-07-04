package com.caitu99.gateway.gateway.pipes.impl;

import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.gateway.exception.CarmenException;
import com.caitu99.gateway.gateway.cache.CarmenApiCache;
import com.caitu99.gateway.gateway.exception.PipelineException;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.model.RequestState;
import com.caitu99.gateway.utils.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class DispatchPipe extends AbstractPipe {

    private static Logger logger = LoggerFactory.getLogger(DispatchPipe.class);

    private AppConfig appConfig;
    private CarmenApiCache apiCache;

    public DispatchPipe() {
        this.appConfig = SpringContext.getBean(AppConfig.class);
        this.apiCache = SpringContext.getBean(CarmenApiCache.class);
    }

    @Override
    public void onEvent(RequestEvent event) {
        try {
            logger.debug("begin of dispatching client request: {}", event);

            // get api configuration
            CarmenApi carmenApi = apiCache.get(event.getId(), event.getNamespace(), event.getMethod(),
                    event.getVersion(), appConfig.env);

            // check api
            if (carmenApi == null) {
                throw new PipelineException(5001, "namespace or method is null");
            }

            // check the switch
            if (carmenApi.getValidFlag() == null || carmenApi.getValidFlag() != 1) {
                throw new PipelineException(5002, "service unavailable");
            }

            // check call type
            if (carmenApi.getApiType() == 2) { // for php
                event.setState(RequestState.ASYNC);
            } else {
                throw new PipelineException(5003, "wrong service type");
            }

            logger.info("complete dispatch: {}", event.getId());

        } catch (CarmenException e) {
            event.setException(e);
            logger.info("exception happened when dispatching", e);
        } catch (Exception e) {
            event.setException(e);
            logger.error("exception happened when dispatching", e);
        } finally {
            logger.debug("end of dispatching client request: {}", event);
            onNext(event);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    protected void onNext(RequestEvent event) {
        if (event.getException() != null)
            event.setState(RequestState.ERROR);

        // go on
        Pipeline.getInstance().process(event);
    }

}
