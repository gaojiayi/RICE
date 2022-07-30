package com.gaojy.rice.dispatcher.scheduler;

import com.alipay.remoting.LifeCycleException;

/**
 * @author gaojy
 * @ClassName LifeCycle.java
 * @Description 
 * @createTime 2022/02/12 13:53:00
 */
public interface LifeCycle {
    void startup() throws LifeCycleException;

    void shutdown() throws LifeCycleException;

    boolean isStarted();
}
