package com.gaojy.rice.dispatcher.scheduler;

import com.alipay.remoting.LifeCycleException;
import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.timewheel.HashedWheelTimer;
import com.gaojy.rice.common.timewheel.Timeout;
import com.gaojy.rice.common.timewheel.TimerTask;
import com.gaojy.rice.dispatcher.common.DispatcherAPIWrapper;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author gaojy
 * @ClassName TaskScheduleManager.java
 * @Description 任务调度管理器
 * @createTime 2022/02/12 01:00:00
 */
public class TaskScheduleManager implements SchedulerManager, Runnable, LifeCycle {

    private final ScheduledExecutorService heartBeatTimer = Executors.newSingleThreadScheduledExecutor();

    private Map<String/* taskCode */, TaskScheduleClient/*TaskScheduleClient*/> clients = new ConcurrentHashMap<>();

    private volatile boolean isStopped = true;

    private final DispatcherAPIWrapper outApiWrapper;
    private  final HashedWheelTimer scheduleTimer = new HashedWheelTimer(new RiceThreadFactory("scheduleTimer"));
    public TaskScheduleManager(DispatcherAPIWrapper outApiWrapper) {
        this.outApiWrapper = outApiWrapper;
    }

    @Override
    public void addTask() {

    }

    @Override public void deleteTask(String taskCode) {

    }

    @Override public void updateTask() {

    }

    @Override public void isolationProcessorForAllTasks(String address) {

    }

    @Override public void taskOnline(String processorAddress, String taskCode) {

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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            });

        }
        this.heartBeatTimer.schedule(this, 2, TimeUnit.SECONDS);
    }

    @Override
    public void startup() throws LifeCycleException {
        this.heartBeatTimer.schedule(this, 2, TimeUnit.SECONDS);
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
}
