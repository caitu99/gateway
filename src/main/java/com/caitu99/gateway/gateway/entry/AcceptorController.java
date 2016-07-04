package com.caitu99.gateway.gateway.entry;

import com.caitu99.gateway.gateway.Constants;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.model.RequestState;
import com.caitu99.gateway.gateway.model.ValidateType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;


@Controller
public class AcceptorController {

    private static final Logger logger = LoggerFactory.getLogger(AcceptorController.class);

    /**
     * the token entry for gateway
     */
    @RequestMapping(value = "/gw/oauthentry/{namespace}/{version}/{method}", produces = "application/json;charset=utf-8")
    public DeferredResult<Object> token(HttpServletRequest request,
                                        @PathVariable String namespace,
                                        @PathVariable String version,
                                        @PathVariable String method) {
        DeferredResult<Object> deferredResult = new DeferredResult<>();
        RequestEvent event = new RequestEvent(deferredResult, request);
        event.getTicTac().tic(Constants.ST_ALL);
        event.setValidateType(ValidateType.TOKEN);
        if (StringUtils.isEmpty(namespace) || StringUtils.isEmpty(method)) {
            ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                    String.format(Constants.ERROR_RESPONSE, 5000, "namespace or method is null"),
                    HttpStatus.valueOf(200));
            deferredResult.setResult(responseEntity);
            return deferredResult;
        }
        if (StringUtils.isNotEmpty(request.getParameter("default"))) {
            ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                    String.format(Constants.ERROR_RESPONSE,  5125, "default cannot be parameter"),
                    HttpStatus.valueOf(200));
            deferredResult.setResult(responseEntity);
            return deferredResult;
        }
        event.setNamespace(namespace);
        event.setMethod(method);
        event.setVersion(version);
        event.setState(RequestState.PARSE);
        Pipeline.getInstance().process(event);
        logger.debug("a request has been putted to queue: " + event);
        return deferredResult;
    }

    /**
     * the sign entry for gateway
     */
    @RequestMapping(value = "/gw/entry/{namespace}/{version}/{method}", produces = "application/json;charset=utf-8")
    public DeferredResult<Object> sign(HttpServletRequest request,
                                       @PathVariable String namespace,
                                       @PathVariable String version,
                                       @PathVariable String method) {
        DeferredResult<Object> deferredResult = new DeferredResult<>();
        RequestEvent event = new RequestEvent(deferredResult, request);
        event.getTicTac().tic(Constants.ST_ALL);
        event.setValidateType(ValidateType.SIGN);
        if (StringUtils.isEmpty(namespace) || StringUtils.isEmpty(method)) {
            ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                    String.format(Constants.ERROR_RESPONSE, 5000, "namespace or method is null"),
                    HttpStatus.valueOf(200));
            deferredResult.setResult(responseEntity);
            return deferredResult;
        }
        if (StringUtils.isNotEmpty(request.getParameter("default"))) {
            ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                    String.format(Constants.ERROR_RESPONSE,  5125, "default cannot be parameter"),
                    HttpStatus.valueOf(200));
            deferredResult.setResult(responseEntity);
            return deferredResult;
        }
        event.setNamespace(namespace);
        event.setMethod(method);
        event.setVersion(version);
        event.setState(RequestState.PARSE);
        Pipeline.getInstance().process(event);
        logger.debug("a request has been putted to queue: " + event);
        return deferredResult;
    }

}
