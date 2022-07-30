package com.gaojy.rice.common.exception;

/**
 * @author gaojy
 * @ClassName DispatcherException.java
 * @Description 
 * @createTime 2022/02/07 12:52:00
 */
public class DispatcherException extends RuntimeException{
    public DispatcherException(String message) {
        super(message);
    }

    public DispatcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public DispatcherException(Throwable cause) {
        super(cause);
    }

    protected DispatcherException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
