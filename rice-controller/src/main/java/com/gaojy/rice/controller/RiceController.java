package com.gaojy.rice.controller;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.controller.election.LeaderStateListener;
import com.gaojy.rice.repository.api.Repository;
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

    private final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class).getExtension("mysql");

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
    // 处理调度器的任务长轮询  处理调度器的处理器状态上报

    // handler处理器注册

    //

    /**
     * master需要处理任务分配
     */

    // 另外启动http用于控制台操作
    public Boolean isLongPollingEnable() {
        return true;
    }

    public Long getShortPollingTimeMills() {
        return 1000L;
    }
    public Repository getRepository(){
        return this.repository;
    }
}
