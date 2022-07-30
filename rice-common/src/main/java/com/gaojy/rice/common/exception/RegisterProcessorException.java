package com.gaojy.rice.common.exception;

/**
 * @author gaojy
 * @ClassName RegisterProcessorException.java
 * @Description 
 * @createTime 2022/01/07 23:30:00
 */
public class RegisterProcessorException extends ProcessorException {
    private static final long serialVersionUID = 3141568510679208975L;

    public RegisterProcessorException(String message) {
        super(message);
    }

    public RegisterProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
