package com.gaojy.rice.processor.api.config;

/**
 * @author gaojy
 * @ClassName constants.java
 * @Description
 * @createTime 2022/01/06 21:27:00
 */
public class ConfigConstants {
    public static final String LISTEN_PORT = "rice.controller.port";

    public static final String DEFAULT_LISTEN_PORT = "8888";

    public static final String SERVER_WORKER_THREADS = "server.worker.threads";

    public static final String SERVER_SELECTOR_THREADS = "server.selector.threads";

    public static final String SERVER_CALLBACK_EXECUTOR_THREADS = "server.callback.executor.threads";

    public static final String SERVER_ONEWAY_SEMAPHORE_VALUE = "server.oneway.semaphore.value";

    public static final String SERVER_ASYNC_SEMAPHORE_VALUE = "server.async.semaphore.value";

    public static final String SERVER_CHANNEL_MAXIDLE_TIME_SECONDS = "server.channel.max.idleTime.seconds";

    public static final String SERVER_POOLED_BYTEBUF_ALLOCATOR_ENABLE = "server.pooled.bytebuf.allocator.enable";

    public static final String RICE_APPLICATION_ID = "rice.application.id";

    public static final String RICE_CONTROLLER_ADDRESS = "rice.controller.address";

    public static final String RICE_PROCESSOR_SCAN_PACKAGE_KEY = "rice.processor.scan.packages";

    public static final String RICE_CONTROLLER_BALANCE = "rice.controller.balance";
}
