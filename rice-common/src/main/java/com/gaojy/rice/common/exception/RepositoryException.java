package com.gaojy.rice.common.exception;

/**
 * @author gaojy
 * @ClassName RepositoryException.java
 * @Description 
 * @createTime 2022/01/17 19:12:00
 */
public class RepositoryException extends RuntimeException {

    private static final long serialVersionUID = 6652837909587360895L;

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }
}
