package com.gaojy.rice.common.protocol.header.scheduler;

import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;

/**
 * @author gaojy
 * @ClassName SchedulerRegisterRequestHeader.java
 * @Description 
 * @createTime 2022/02/10 20:41:00
 */
public class SchedulerRegisterRequestHeader implements CommandCustomHeader {

    /**
     * 是否第第一次启动
     */
    private Boolean isFirstRegister;

    /**
     * 调度器启动时间
     */
    private Long bootTime;

    @Override
    public void checkFields() throws RemotingCommandException {

    }

    public Boolean getFirstRegister() {
        return isFirstRegister;
    }

    public void setFirstRegister(Boolean firstRegister) {
        isFirstRegister = firstRegister;
    }

    public Long getBootTime() {
        return bootTime;
    }

    public void setBootTime(Long bootTime) {
        this.bootTime = bootTime;
    }
}
