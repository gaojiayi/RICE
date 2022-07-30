package com.gaojy.rice.common.protocol.header.scheduler;

import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;

/**
 * @author gaojy
 * @ClassName SchedulerHeartBeatHeader.java
 * @Description 
 * @createTime 2022/02/07 13:38:00
 */
public class SchedulerHeartBeatHeader implements CommandCustomHeader {
    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
