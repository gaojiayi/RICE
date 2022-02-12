package com.gaojy.rice.dispatcher.scheduler;

/**
 * @author gaojy
 * @ClassName ProcessorNotifyAble.java
 * @Description TODO
 * @createTime 2022/02/11 17:32:00
 */
public interface ProcessorNotifyAble {
    public void notifyProcessor(String addressStr, int status);
}
