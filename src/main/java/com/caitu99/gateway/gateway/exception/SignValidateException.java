package com.caitu99.gateway.gateway.exception;


import com.caitu99.gateway.exception.CarmenException;

public class SignValidateException extends CarmenException {

    public SignValidateException(int code, String error) {
        super(code, error);
    }

    /*
        use this method to format message
     */
    public String getMessage() {
        return super.getMessage();
    }

}
