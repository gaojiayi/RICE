package com.gaojy.rice.common.protocol.body.scheduler;

import com.gaojy.rice.common.protocol.RemotingSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * 执行结果
 */
public class TaskInvokerResponseBody extends RemotingSerializable {

    private Map resultMap = new HashMap<>();

    public Map getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map resultMap) {
        this.resultMap = resultMap;
    }
}
