package com.gaojy.rice.common.exception;

/**
 * @author gaojy
 * @ClassName DuplicateProcessorException.java
 * @Description 
 * @createTime 2022/01/07 15:57:00
 */
public class DuplicateProcessorException extends ProcessorException {

    private static final long serialVersionUID = -3367751371841579762L;

    public DuplicateProcessorException(String message) {
        super(message);
    }

    public DuplicateProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
