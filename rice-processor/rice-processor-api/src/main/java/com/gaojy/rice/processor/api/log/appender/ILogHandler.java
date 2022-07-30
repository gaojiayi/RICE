package com.gaojy.rice.processor.api.log.appender;

import io.netty.channel.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaojy
 * @ClassName ILogHandler.java
 * @Description 
 * @createTime 2022/07/28 11:14:00
 */
public interface ILogHandler {
    public static final Map<Long/* task_instance_id */,Channel/* channel */> schedulersOfLog = new ConcurrentHashMap<>();
}
