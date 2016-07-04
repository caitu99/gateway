package com.caitu99.gateway.gateway.pipes;


import com.caitu99.gateway.gateway.model.RequestEvent;

public interface IPipe {

    /*
        process request event
     */
    void onEvent(RequestEvent event);

}
