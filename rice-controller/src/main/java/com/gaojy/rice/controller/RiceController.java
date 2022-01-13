package com.gaojy.rice.controller;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.controller.election.LeaderStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RiceController.java
 * @Description TODO
 * @createTime 2022/01/08 10:39:00
 */
public class RiceController implements LeaderStateListener {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);

    @Override
    public void onLeaderStart(long leaderTerm) {
        // 扫描未被托管的task

    }

    @Override
    public void onLeaderStop(long leaderTerm) {

    }

    // 启动选举

    // 启动业务服务端

    // 如果是主控制器，则启动一系列的维护调度 比如定时打印等

    /**
     * master需要处理任务分配
     */





}
