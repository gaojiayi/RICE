package com.gaojy.rice.remote.transport;

/**
 * @author gaojy
 * @ClassName RiceSystemConfig.java
 * @Description TODO
 * @createTime 2022/01/02 10:37:00
 */
public class TransfSystemConfig {

    public static final String COM_RICE_REMOTING_NETTY_POOLED_BYTE_BUF_ALLOCATOR_ENABLE =
        "com.rice.remoting.nettyPooledByteBufAllocatorEnable";
    public static final String COM_RICE_REMOTING_SOCKET_SNDBUF_SIZE = //
        "com.rice.remoting.socket.sndbuf.size";
    public static final String COM_RICE_REMOTING_SOCKET_RCVBUF_SIZE = //
        "com.rice.remoting.socket.rcvbuf.size";
    public static final String COM_RICE_REMOTING_CLIENT_ASYNC_SEMAPHORE_VALUE = //
        "com.rice.remoting.clientAsyncSemaphoreValue";
    public static final String COM_RICE_REMOTING_CLIENT_ONEWAY_SEMAPHORE_VALUE = //
        "com.rice.remoting.clientOnewaySemaphoreValue";
    public static final boolean NETTY_POOLED_BYTE_BUF_ALLOCATOR_ENABLE = //
        Boolean
            .parseBoolean(System.getProperty(COM_RICE_REMOTING_NETTY_POOLED_BYTE_BUF_ALLOCATOR_ENABLE, "false"));
    public static final int CLIENT_ASYNC_SEMAPHORE_VALUE = //
        Integer.parseInt(System.getProperty(COM_RICE_REMOTING_CLIENT_ASYNC_SEMAPHORE_VALUE, "65535"));
    public static final int CLIENT_ONEWAY_SEMAPHORE_VALUE = //
        Integer.parseInt(System.getProperty(COM_RICE_REMOTING_CLIENT_ONEWAY_SEMAPHORE_VALUE, "65535"));
    public static int socketSndbufSize = //
        Integer.parseInt(System.getProperty(COM_RICE_REMOTING_SOCKET_SNDBUF_SIZE, "65535"));
    public static int socketRcvbufSize = //
        Integer.parseInt(System.getProperty(COM_RICE_REMOTING_SOCKET_RCVBUF_SIZE, "65535"));
}
