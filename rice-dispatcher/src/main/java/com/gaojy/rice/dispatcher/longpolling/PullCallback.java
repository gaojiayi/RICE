package com.gaojy.rice.dispatcher.longpolling;

/**
 * @author gaojy
 * @ClassName PullCallback.java
 * @Description 
 * @createTime 2022/02/10 21:20:00
 */
public interface PullCallback {
    void onSuccess(final PullResult pullResult);

    void onException(final Throwable e);
}
