package com.gaojy.rice.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gaojy
 * @ClassName RiceThreadFactory.java
 * @Description TODO
 * @createTime 2022/01/01 22:52:00
 */
public class RiceThreadFactory implements ThreadFactory {
    public final String threadName;
    public final AtomicInteger threadIndex = new AtomicInteger(0);

    public RiceThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "threadName" + this.threadIndex.incrementAndGet());
    }
}
