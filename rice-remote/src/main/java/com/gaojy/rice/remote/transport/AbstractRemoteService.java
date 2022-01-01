package com.gaojy.rice.remote.transport;

import com.gaojy.rice.remote.common.RemoteHelper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName AbstractRemoteService.java
 * @Description 作为RPC服务端和客户端的基础类
 * @createTime 2022/01/01 13:40:00
 */
public abstract class AbstractRemoteService {

    private static final Logger log = LoggerFactory.getLogger(RemoteHelper.RICE_REMOTING);

    private final ConcurrentMap<String /* addr */, ChannelWrapper> channelTables = new ConcurrentHashMap<String, ChannelWrapper>();


}
