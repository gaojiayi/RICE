package com.gaojy.rice.common.protocol.header.scheduler;

import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;

/**
 * @author gaojy
 * @ClassName SchedulerPullTaskChangeRequestHeader.java
 * @Description TODO
 * @createTime 2022/02/10 21:00:00
 */
public class SchedulerPullTaskChangeRequestHeader implements CommandCustomHeader {
    private Long lastTaskChangeTimestamp = -1L;
    @Override
    public void checkFields() throws RemotingCommandException {

    }

    public Long getLastTaskChangeTimestamp() {
        return lastTaskChangeTimestamp;
    }

    public void setLastTaskChangeTimestamp(Long lastTaskChangeTimestamp) {
        this.lastTaskChangeTimestamp = lastTaskChangeTimestamp;
    }
}
