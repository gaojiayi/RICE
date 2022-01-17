package com.gaojy.rice.common.exception;

/**
 * @author gaojy
 * @ClassName RepositoryConnectionException.java
 * @Description TODO
 * @createTime 2022/01/17 19:13:00
 */
public class RepositoryConnectionException extends RepositoryException{

    public RepositoryConnectionException(String message) {
        super(message);
    }

    public RepositoryConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
