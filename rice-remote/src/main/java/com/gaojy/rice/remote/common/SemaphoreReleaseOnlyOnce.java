package com.gaojy.rice.remote.common;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author gaojy
 * @ClassName SemaphoreReleaseOnlyOnce.java
 * @Description TODO
 * @createTime 2022/01/02 17:33:00
 */
public class SemaphoreReleaseOnlyOnce {

    private final AtomicBoolean released = new AtomicBoolean(false);

    private final Semaphore semaphore;

    public SemaphoreReleaseOnlyOnce(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public void release() {
        if (this.semaphore != null) {
            if (this.released.compareAndSet(false, true)) {
                this.semaphore.release();
            }
        }
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}
