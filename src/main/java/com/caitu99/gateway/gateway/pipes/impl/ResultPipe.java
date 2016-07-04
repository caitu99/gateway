package com.caitu99.gateway.gateway.pipes.impl;


import com.alibaba.fastjson.JSONObject;
import com.caitu99.gateway.gateway.exception.ResultException;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import com.caitu99.gateway.gateway.model.ApiResponse;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.model.RequestState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;


@SuppressWarnings("ALL")
public class ResultPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(ResultPipe.class);

    public ResultPipe() {
    }

    @Override
    public void onEvent(RequestEvent event) {
        try {
            logger.debug("begin of processing result: {}", event);

            DeferredResult<Object> eventResult = event.getResult();

            // check if expired
            if (eventResult.isSetOrExpired()) {
                logger.warn("a request has been expired: " + event);
                return;
            }

            try {
                if (!StringUtils.isEmpty(event.getResultStr())) {
                    ApiResponse apiResponse = JSONObject.parseObject(event.getResultStr(), ApiResponse.class);
                    if (apiResponse.getCode() == 0) {
                        ResponseEntity<String> responseEntity = new ResponseEntity<String>(event.getResultStr(), HttpStatus.valueOf(200));
                        eventResult.setResult(responseEntity);
                        logger.info("get correct response, event: {}, result: {}", event,event.getResultStr());
                    } else {
                        ResponseEntity<String> responseEntity = new ResponseEntity<String>(event.getResultStr(), HttpStatus.valueOf(200));
                        eventResult.setResult(responseEntity);
                        logger.info("get error response, event: {}, result: {}", event, event.getResultStr());
                    }
                } else {
                    logger.warn("get empty result: {}", event);
                }
            } catch (Exception e) {
                logger.error("incorrect format: {}", event, e);
                event.setException(e);
            }

        } catch (Exception e) {
            logger.error("exception happened when processing result: {}", event.getId(), e);
        } finally {
            logger.debug("complete processing result: {}", event);
            onNext(event);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    protected void onNext(RequestEvent event) {
        if (event.getException() != null)
            if(event.getException() instanceof ResultException)
                event.setState(RequestState.POST);
            else
                event.setState(RequestState.ERROR);
        else
            event.setState(RequestState.POST);

        // go on
        Pipeline.getInstance().process(event);
    }
}
