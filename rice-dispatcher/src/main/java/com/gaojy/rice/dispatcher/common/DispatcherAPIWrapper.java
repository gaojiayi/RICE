package com.gaojy.rice.dispatcher.common;

import com.gaojy.rice.dispatcher.RiceDispatchScheduler;
import com.gaojy.rice.remote.transport.TransportClient;

/**
 * @author gaojy
 * @ClassName DispatcherAPIWrapper.java
 * @Description TODO
 * @createTime 2022/02/09 22:35:00
 */
public class DispatcherAPIWrapper {
    private final RiceDispatchScheduler riceDispatchScheduler;
    private final TransportClient transportClient;

    public DispatcherAPIWrapper(RiceDispatchScheduler riceDispatchScheduler) {
        this.riceDispatchScheduler = riceDispatchScheduler;
        transportClient = this.riceDispatchScheduler.getTransportClient();
    }
}
