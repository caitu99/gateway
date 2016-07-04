package com.caitu99.gateway.gateway.pipes.impl;

import com.alibaba.fastjson.JSONException;
import com.caitu99.gateway.exception.CarmenException;
import com.caitu99.gateway.gateway.Constants;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import com.caitu99.gateway.gateway.excutor.TicTac;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.model.RequestState;
import com.caitu99.gateway.monitor.constants.GatewayConstant;
import com.caitu99.gateway.monitor.service.MonitorService;
import com.caitu99.gateway.utils.SpringContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class ErrorPipe extends AbstractPipe {

    private static Logger logger = LoggerFactory.getLogger(ErrorPipe.class);

    private MonitorService monitorService;
    
    public ErrorPipe() {

        this.monitorService = SpringContext.getBean(MonitorService.class);
    }

    @Override
    public void onEvent(RequestEvent event) {
    	long all = 0;
        long call = 0;
        long pre = 0;
        long redis = 0;
        String exceptionName = "Excepiton";
        try {
            logger.debug("begin error process: {}", event);
            DeferredResult<Object> eventResult = event.getResult();

            // check if expired
            if (eventResult.isSetOrExpired()) {
                logger.warn("a request has been expired: " + event);
                return;
            }

            Exception exception = event.getException();

            if (exception != null) {
                logger.info("exception happened, context: {}", event);
                if (exception instanceof CarmenException) {
                    CarmenException e = (CarmenException) exception;
                    ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                            String.format(Constants.ERROR_RESPONSE, e.getCode(), e.getDescription()),
                            HttpStatus.valueOf(200));
                    eventResult.setResult(responseEntity);
                    exceptionName = "CarmenException";
                } else if (exception instanceof ExecutionException) {
                    ExecutionException e = (ExecutionException) exception;
                    ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                            String.format(Constants.ERROR_RESPONSE, 5004, e.getMessage()),
                            HttpStatus.valueOf(200));
                    eventResult.setResult(responseEntity);
                    exceptionName = "ExecutionException";
                } else if (exception instanceof JSONException) {
                    ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                            String.format(Constants.ERROR_RESPONSE, 5005, "incorrect response format: " + event.getResultStr()),
                            HttpStatus.valueOf(200));
                    eventResult.setResult(responseEntity);
                    exceptionName = "JSONException";
                } else {
                    ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                            String.format(Constants.ERROR_RESPONSE, -1, exception.getMessage()),
                            HttpStatus.valueOf(200));
                    eventResult.setResult(responseEntity);
                }
            } else {
                logger.error("exception happened when processing error, exception is null");
            }
        } catch (Exception e) {
            logger.error("exception happened when processing error", e);
        } finally {
            logger.debug("complete error process: {}", event);
            event.getTicTac().tac(Constants.ST_ALL);
            TicTac ticTac = event.getTicTac();

            Map<String, Long> perfMap = new HashMap<>();

            for (Map.Entry<String, TicTac.STEntry> entry : ticTac.getEntryMap().entrySet()) {

                if (entry.getKey().equals(Constants.ST_PRE_PIPE)) {
                    pre = entry.getValue().getElapsed();
                } else if (entry.getKey().equals(Constants.ST_REDIS_TOKEN)) {
                    redis = entry.getValue().getElapsed();
                } else if (entry.getKey().equals(Constants.ST_ALL)) {
                    all = entry.getValue().getElapsed();
                } else if (entry.getKey().equals(Constants.ST_CALL)) {
                    call = entry.getValue().getElapsed();
                }

                if (!entry.getKey().equals(Constants.ST_POST))
                    perfMap.put(entry.getKey(), entry.getValue().getElapsed());
            }
            
            monitorService.saveMonitorData(event, all, call, pre, redis, GatewayConstant.FAILUE, exceptionName);
            onNext(event);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    protected void onNext(RequestEvent event) {
        event.setState(RequestState.COMPLETE);

        // go on
        Pipeline.getInstance().process(event);
    }
}
