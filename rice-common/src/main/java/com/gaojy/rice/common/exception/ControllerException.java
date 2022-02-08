package com.gaojy.rice.common.exception;

/**
 * @author gaojy
 * @ClassName ControllerException.java
 * @Description TODO
 * @createTime 2022/02/07 12:52:00
 */
public class ControllerException extends RuntimeException{
    public ControllerException(String message) {
        super(message);
    }

    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerException(Throwable cause) {
        super(cause);
    }

    protected ControllerException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
