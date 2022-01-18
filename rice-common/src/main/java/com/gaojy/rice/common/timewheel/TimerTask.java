package com.gaojy.rice.common.timewheel;

/**
 * A task which is executed after the delay specified with
 * Timer#newTimeout(TimerTask, long, TimeUnit).
 *
 * Forked from <a href="https://github.com/netty/netty">Netty</a>.
 */
public interface TimerTask {

    /**
     * Executed after the delay specified with
     * Timer#newTimeout(TimerTask, long, TimeUnit).
     *
     * @param timeout a handle which is associated with this task
     */
    void run(final Timeout timeout) throws Exception;
}
