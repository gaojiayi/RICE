package com.gaojy.rice.dispatcher.scheduler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 调度器接口
 */
public interface RiceScheduler {

    ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit);

    ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period,
                                           final TimeUnit unit);

    ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay,
                                              final TimeUnit unit);

    void shutdown();
}
