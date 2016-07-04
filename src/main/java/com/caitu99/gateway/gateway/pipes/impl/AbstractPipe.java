package com.caitu99.gateway.gateway.pipes.impl;


import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.pipes.IPipe;

public class AbstractPipe implements IPipe {

    @Override
    public void onEvent(RequestEvent event) {
    }

    protected void onNext(RequestEvent event) {
    }

}
