package com.caitu99.gateway.apiconfig.exception;

/**
 * 远程服务异常
 *
 * @author: chenyun
 * @since: 2015年6月5日 下午1:55:23
 * @history:
 */
public class RemoteServiceException extends Exception {

    private static final long serialVersionUID = -238091758285157331L;

    public RemoteServiceException() {
        super();
    }

    public RemoteServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteServiceException(String message) {
        super(message);
    }

    public RemoteServiceException(Throwable cause) {
        super(cause);
    }

}
