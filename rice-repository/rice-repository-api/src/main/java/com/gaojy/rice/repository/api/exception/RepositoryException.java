package com.gaojy.rice.repository.api.exception;

/**
 * @author gaojy
 * @ClassName exception.java
 * @Description TODO
 * @createTime 2022/01/28 12:14:00
 */
public class RepositoryException  extends RuntimeException{

    private static final long serialVersionUID = 3386344117626069333L;

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
