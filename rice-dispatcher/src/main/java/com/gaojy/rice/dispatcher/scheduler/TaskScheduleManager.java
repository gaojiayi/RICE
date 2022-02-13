package com.gaojy.rice.dispatcher.scheduler;

import com.alipay.remoting.LifeCycleException;
import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.TaskOptType;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.common.timewheel.HashedWheelTimer;
import com.gaojy.rice.dispatcher.common.DispatcherAPIWrapper;
import com.gaojy.rice.dispatcher.longpolling.PullTaskService;
import com.gaojy.rice.repository.api.Repository;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TaskScheduleManager.java
 * @Description 任务调度管理器
 * @createTime 2022/02/12 01:00:00
 */
public class TaskScheduleManager implements SchedulerManager, Runnable, LifeCycle {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);

    private final ScheduledExecutorService heartBeatTimer = Executors.newSingleThreadScheduledExecutor();

    private Map<String/* taskCode */, TaskScheduleClient/*TaskScheduleClient*/> clients = new ConcurrentHashMap<>();

    private volatile boolean isStopped = true;

    private final DispatcherAPIWrapper outApiWrapper;

    private final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class).getExtension("mysql");

    private final PullTaskService pullTaskService;

    private final HashedWheelTimer scheduleTimer = new HashedWheelTimer(new RiceThreadFactory("scheduleTimer"));

    public TaskScheduleManager(DispatcherAPIWrapper outApiWrapper) {
        this.outApiWrapper = outApiWrapper;
        this.pullTaskService = new PullTaskService(outApiWrapper, this);
    }

    @Override
    public void addTask(RiceTaskInfo info, List<ProcessorServerInfo> processorServerInfoList) {

    }

    public void deleteTask(String taskCode) {

    }

    private void updateTask(String taskCode) {

    }

    private synchronized void taskProcessorChange(String taskCode) {
        // 包括了taskcode的上线和下线

    }

    @Override
    public void onChange(TaskChangeRecord record) {
        TaskOptType type = TaskOptType.getTaskOptType(record.getOptType());
        if(taskIsRunning(record.getTaskCode())){
            switch (type) {
                case TASK_PROCESSOR_ONLINE:
                case TASK_PROCESSOR_OFFLINE:
                    this.taskProcessorChange(record.getTaskCode());
                    break;
                case TASK_DELETE:
                    this.deleteTask(record.getTaskCode());
                    break;
                case TASK_UPDATE:
                    this.updateTask(record.getTaskCode());
                    break;
                default:
                    log.warn("Unknown task action type received");
            }
        }else {
            log.warn("The task:{} has stopped running",record.getTaskCode());
        }


    }

    private boolean taskIsRunning(String taskCode){
        return clients.containsKey(taskCode) && clients.get(taskCode).isStarted();
    }

    @Override
    public void taskReBalance(String currentScheduler, List<String> schedulerList) {
        // 查询所有的taskcode

        // 根据一致性hash算法，获取落在当前调度器上的taskCode

        // 再次查询数据库，初始化这些task
    }

    @Override
    public void isolationProcessorForAllTasks(String address) {

    }

    // 全局心跳
    @Override
    public void run() {
        if (!clients.isEmpty()) {
            Set<String> addresses = new HashSet<>();
            clients.values().forEach(client -> {
                addresses.addAll(client.getProcesses());
            });
            addresses.forEach(address -> {
                try {
                    outApiWrapper.heartBeatToProcessor(address);
                } catch (InterruptedException | TimeoutException e) {
                    log.error("Heartbeat sent to processor failed, address:{},error:{}", address, e);
                }
            });

        }
        this.heartBeatTimer.schedule(this, 2, TimeUnit.SECONDS);
    }

    @Override
    public void startup() throws LifeCycleException {
        pullTaskService.start();
        run();
        isStopped = false;
    }

    @Override
    public void shutdown() throws LifeCycleException {
        this.heartBeatTimer.shutdown();
        isStopped = true;
    }

    @Override public boolean isStarted() {
        return !isStopped;
    }

    public DispatcherAPIWrapper getOutApiWrapper() {
        return outApiWrapper;
    }

    public PullTaskService getPullTaskService() {
        return pullTaskService;
    }

    public HashedWheelTimer getScheduleTimer() {
        return scheduleTimer;
    }
}
