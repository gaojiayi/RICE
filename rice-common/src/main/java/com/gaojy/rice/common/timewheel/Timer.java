package com.gaojy.rice.common.timewheel;

import java.util.Set;

import java.util.concurrent.TimeUnit;

public interface Timer {

    Timeout newTimeout(final TimerTask task, final long delay, final TimeUnit unit);

    Set<Timeout> stop();
}
