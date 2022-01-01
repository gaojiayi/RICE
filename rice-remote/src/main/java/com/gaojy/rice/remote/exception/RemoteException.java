package com.gaojy.rice.remote.exception;

/**
 * @author gaojy
 * @ClassName RemoteException.java
 * @Description TODO
 * @createTime 2022/01/01 14:29:00
 */
public class RemoteException extends Exception {

    private static final long serialVersionUID = -5146846388412313917L;

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
