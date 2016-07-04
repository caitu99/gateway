package com.caitu99.gateway.gateway.pipes.impl;

import com.caitu99.gateway.gateway.Constants;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.model.RequestState;
import com.caitu99.gateway.gateway.model.ValidateType;
import com.caitu99.gateway.gateway.services.HelpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public class ParsePipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(ParsePipe.class);

    public void onEvent(RequestEvent event) {
        try {
            logger.debug("begin of parsing client request: {}", event);

            HttpServletRequest request = event.getRequest();

            Map<String, String> params = event.getParams();
            Map<String, String> signParams = event.getSignParams();
            Map<String, String[]> parameterMap = request.getParameterMap();

            // set event ip
            String ip = HelpService.getIp(request);
            event.setIp(ip);

            // set normal parameters
            // caution: only get the first parameter
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                if (entry.getKey().equals(Constants.ACCESS_TOKEN)) {
                    event.setAccessToken(entry.getValue()[0]);
                } else if (entry.getKey().equals(Constants.SIGN)) {
                    event.setSign(entry.getValue()[0]);
                } else {
                    String value = entry.getValue()[0];
                    params.put(entry.getKey(), value);
                    signParams.put(entry.getKey(), value);
                }
            }

            // set multi files
            if (request instanceof MultipartHttpServletRequest) {
                event.setMultiFiles(((MultipartHttpServletRequest) request).getMultiFileMap());
            }

        } catch (Exception e) {
            event.setException(e);
            logger.error("exception happened when parsing request: {}", event.getId(), e);
        } finally {
            logger.info("complete parsing request: {}", event.getId());
            onNext(event);
        }
    }

    @Override
    protected void onNext(RequestEvent event) {
        if (event.getValidateType() == ValidateType.TOKEN)
            event.setState(RequestState.TOKEN);
        else if (event.getValidateType() == ValidateType.SIGN)
            event.setState(RequestState.SIGN);

        // go on
        Pipeline.getInstance().process(event);
    }

}
