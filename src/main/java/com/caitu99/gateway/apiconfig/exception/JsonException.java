package com.caitu99.gateway.apiconfig.exception;

/**
 * @author: chenyun
 * @since: 2015年6月5日 下午1:55:23
 * @history:
 */
public class JsonException extends RuntimeException {

    private static final long serialVersionUID = -238091758285157331L;

    public JsonException() {
        super();
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

}
