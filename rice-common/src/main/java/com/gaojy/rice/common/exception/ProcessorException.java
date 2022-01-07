package com.gaojy.rice.common.exception;

/**
 * @author gaojy
 * @ClassName ProcessorException.java
 * @Description TODO
 * @createTime 2022/01/07 23:34:00
 */
public class ProcessorException extends Exception {

    private static final long serialVersionUID = -1986679836705067508L;

    public ProcessorException(String message) {
        super(message);
    }

    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
