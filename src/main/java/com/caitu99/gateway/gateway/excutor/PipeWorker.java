package com.caitu99.gateway.gateway.excutor;

import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.pipes.IPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipeWorker implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(PipeWorker.class);

    private String name;
    private IPipe pipe;
    private RequestEvent event;

    /*public PipeWorker(IPipe pipe, RequestEvent event) {
        this.pipe = pipe;
        this.event = event;
    }*/

    public PipeWorker(IPipe pipe, RequestEvent event, String name) {
        this.pipe = pipe;
        this.event = event;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            pipe.onEvent(event);
        } catch (Exception e) {
            // there must be no exception
            logger.error("an exception happened", e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RequestEvent getEvent() {
        return event;
    }

    public void setEvent(RequestEvent event) {
        this.event = event;
    }
}
