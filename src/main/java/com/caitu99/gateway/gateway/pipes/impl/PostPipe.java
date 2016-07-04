package com.caitu99.gateway.gateway.pipes.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.gateway.exception.CarmenException;
import com.caitu99.gateway.gateway.Constants;
import com.caitu99.gateway.gateway.excutor.PerfMonitor;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import com.caitu99.gateway.gateway.excutor.TicTac;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.model.RequestState;
import com.caitu99.gateway.monitor.constants.GatewayConstant;
import com.caitu99.gateway.monitor.entity.Gateway;
import com.caitu99.gateway.monitor.service.MonitorService;
import com.caitu99.gateway.utils.AppUtils;
import com.caitu99.gateway.utils.SpringContext;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class PostPipe extends AbstractPipe {

    private static Logger logger = LoggerFactory.getLogger(PostPipe.class);

    private PerfMonitor perfMonitor;
    private MonitorService monitorService;

    public PostPipe() {
        this.perfMonitor = SpringContext.getBean(PerfMonitor.class);
        this.monitorService = SpringContext.getBean(MonitorService.class);
    }

    @Override
    public void onEvent(RequestEvent event) {

        long all = 0;
        long call = 0;
        long pre = 0;
        long redis = 0;
        String isSuccess = GatewayConstant.TRUE;
        String exceptionName = "";
        try {
            logger.debug("begin of post processing: {}", event);

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

            // log unknown error
            Exception e = event.getException();
            if (e != null && !(e instanceof CarmenException)) {
                logger.error("unknown error", event.getException());
                isSuccess = GatewayConstant.FAILUE;
                exceptionName = "Excetpion";
            }

            logger.info("event: {}, performance of {}.{}: {}", event.getId(), event.getNamespace(), event.getMethod(), JSONObject.toJSON(perfMap));

        } finally {
            logger.debug("end of post processing: {}", event);
            
            monitorService.saveMonitorData(event, all, call, pre, redis, isSuccess, exceptionName);
            
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
