package com.gaojy.rice.dispatcher.scheduler;

import com.alipay.remoting.LifeCycleException;
import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.TaskOptType;
import com.gaojy.rice.common.constants.TaskStatus;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.common.timewheel.HashedWheelTimer;
import com.gaojy.rice.dispatcher.allocation.AllocationType;
import com.gaojy.rice.dispatcher.allocation.TaskAllocationFactory;
import com.gaojy.rice.dispatcher.common.DispatcherAPIWrapper;
import com.gaojy.rice.dispatcher.longpolling.PullTaskService;
import com.gaojy.rice.repository.api.Repository;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
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

    private AllocationType taskAllocationStrategy = AllocationType.CONSISTENTHASH;

    public TaskScheduleManager(DispatcherAPIWrapper outApiWrapper) {
        this.outApiWrapper = outApiWrapper;
        this.pullTaskService = new PullTaskService(outApiWrapper, this);
    }

    @Override
    public void addTask(RiceTaskInfo info, List<ProcessorServerInfo> processorServerInfoList) {
        TaskScheduleClient client = null;
        try {
            client = new TaskScheduleClient(info, this);
            client.initProcessores(processorServerInfoList);
            TaskScheduleClient prev = clients.put(info.getTaskCode(), client);
            if (prev != null) {
                prev.shutdown();
            }
            client.startup();
            // 写数据库
            repository.getRiceTaskInfoDao().addTask(info);
            repository.getProcessorServerInfoDao().batchCreateOrUpdateInfo(processorServerInfoList);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void taskStatusChange(String taskCode, TaskStatus status) {
        TaskScheduleClient client = clients.get(taskCode);
        if (client != null) {
            client.getTaskStatus().set(status);
            if (status == TaskStatus.OFFLINE) {
                client.shutdown();
                clients.remove(taskCode);
            }
        }

        repository.getRiceTaskInfoDao().taskStatusChange(status.getCode());
    }

    private void updateTaskInfo(String taskCode) {
        // TODO

    }

    private void taskProcessorChange(String taskCode) {
        // 包括了taskcode的上线和下线
        TaskScheduleClient client = clients.get(taskCode);
        if (client != null) {
            List<ProcessorServerInfo> servers = repository.getProcessorServerInfoDao().getInfosByTask(taskCode);
            if (CollectionUtils.isNotEmpty(servers)) {
                Set<String> collect = servers.stream().map(server -> {
                    return server.getAddress() + ":" + server.getPort();
                }).collect(Collectors.toSet());
                Set<String> newProcessorList = new CopyOnWriteArraySet<>(collect);
                client.setProcesses(newProcessorList);
            }
        }

    }

    @Override
    public void onChange(TaskChangeRecord record) {
        TaskOptType type = TaskOptType.getTaskOptType(record.getOptType());
        if (taskIsRunning(record.getTaskCode())) {
            switch (type) {
                case TASK_PROCESSOR_ONLINE:
                case TASK_PROCESSOR_OFFLINE:
                    this.taskProcessorChange(record.getTaskCode());
                    break;
                case TASK_DELETE:
                    this.taskStatusChange(record.getTaskCode(), TaskStatus.OFFLINE);
                    break;
                case TASK_PAUSE:
                    this.taskStatusChange(record.getTaskCode(), TaskStatus.PAUSE);
                    break;
                case TASK_RUNNING:
                    this.taskStatusChange(record.getTaskCode(), TaskStatus.ONLINE);
                    break;
                case TASK_UPDATE:
                    this.updateTaskInfo(record.getTaskCode());
                    break;
                default:
                    log.warn("Unknown task action type received");
            }
        } else {
            log.warn("The task:{} has stopped running", record.getTaskCode());
        }

    }

    private boolean taskIsRunning(String taskCode) {
        return clients.containsKey(taskCode) && clients.get(taskCode).isStarted();
    }

    @Override
    public void taskReBalance(String currentScheduler, List<String> schedulerList) {
        // 查询所有的非下线taskcode
        List<String> taskCodes = repository.getRiceTaskInfoDao().getAllValidTaskCode();

        // 根据一致性hash算法，获取落在当前调度器上的taskCode

        List<String> allocationTaskCodes = TaskAllocationFactory.getStrategy(taskAllocationStrategy)
                .allocate(schedulerList, taskCodes).get(currentScheduler);

        // 再次查询数据库，初始化这些task  已有的taskCode - 交
        Collection<String> deleteTaskCode = CollectionUtils.subtract(clients.keySet(), allocationTaskCodes);
        Collection<String> addTaskCode = CollectionUtils.subtract(allocationTaskCodes, clients.keySet());
        deleteTaskCode.forEach(taskCode -> {
            this.taskStatusChange(taskCode, TaskStatus.OFFLINE);
        });
        addTaskCode.forEach(taskCode -> {
            // 根据taskcode查info  和 当前的处理器
            List<String> tasks = new ArrayList<>();
            tasks.add(taskCode);
            List<RiceTaskInfo> infos = repository.getRiceTaskInfoDao().getInfoByCodes(tasks);
            List<ProcessorServerInfo> processores = repository.getProcessorServerInfoDao().getInfosByTask(taskCode);
            if (CollectionUtils.isNotEmpty(infos)) {
                this.addTask(infos.get(0), processores);
            }

        });
    }

    @Override
    public void isolationProcessorForAllTasks(String address) {
        clients.values().stream().filter(client ->{
            return client.getProcesses().stream().anyMatch(processor->{
                return processor.contains(address);
            });
        }).forEach(client ->{
            client.isolationProcessor(address);
        });
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
        pullTaskService.shutdown();
        clients.values().forEach(client -> client.shutdown());
        isStopped = true;
    }

    @Override
    public boolean isStarted() {
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
