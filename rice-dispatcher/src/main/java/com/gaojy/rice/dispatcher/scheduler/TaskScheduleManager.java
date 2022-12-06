package com.gaojy.rice.dispatcher.scheduler;

import com.alipay.remoting.LifeCycleException;
import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.TaskOptType;
import com.gaojy.rice.common.constants.TaskStatus;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.entity.RiceLog;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.exception.DispatcherException;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.common.timewheel.HashedWheelTimer;
import com.gaojy.rice.common.allocation.AllocationType;
import com.gaojy.rice.common.allocation.TaskAllocationFactory;
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

    // 保存任务运行实例
    private Map<String/* taskCode */, TaskScheduleClient/*TaskScheduleClient*/> clients = new ConcurrentHashMap<>();

    private volatile boolean isStopped = true;

    private final DispatcherAPIWrapper outApiWrapper;

    private final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class).getExtension("mysql");

    private final PullTaskService pullTaskService;

    private final HashedWheelTimer scheduleTimer = new HashedWheelTimer(new RiceThreadFactory("scheduleTimer"));

    private AllocationType taskAllocationStrategy = AllocationType.CONSISTENTHASH;

    private Map<String/* ip:port */, Long> processorHeartBeatRecord = new HashMap<>();

    public TaskScheduleManager(DispatcherAPIWrapper outApiWrapper) {
        this.outApiWrapper = outApiWrapper;
        this.pullTaskService = new PullTaskService(outApiWrapper, this);
    }

    @Override
    public void addTask(String currentScheduler, RiceTaskInfo info, List<ProcessorServerInfo> processorServerInfoList) {
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
            info.setSchedulerServer(currentScheduler);
            info.setStatus(TaskStatus.ONLINE.getCode());
            info.setUpdateTime(new Date());
            if(info.getId() > 0) {
                repository.getRiceTaskInfoDao().updateTask(info);
            }else {
                repository.getRiceTaskInfoDao().addTask(info);
            }
        } catch (ParseException e) {
            log.error("add task error ,task code = " + info.getTaskCode(), e);
            throw new DispatcherException(e);
        }

    }

    public void taskStatusChange(String taskCode, TaskStatus status) {
        TaskScheduleClient client = clients.get(taskCode);
        if (client != null) {
            log.info("task_code:{},task_name:{}, status will change to " + status.name(), taskCode, client.getTaskName());
            client.getTaskStatus().set(status);
            if (status == TaskStatus.OFFLINE) {
                client.shutdown();
                clients.remove(taskCode);
            }
        }

        repository.getRiceTaskInfoDao().taskStatusChange(taskCode, status.getCode());
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
                    //case  TASK_PROCESSOR_OFFLINE:
                    this.taskProcessorChange(record.getTaskCode());
                    break;
                case TASK_DELETE:
                    this.taskStatusChange(record.getTaskCode(), TaskStatus.OFFLINE);
                    break;
                case TASK_PAUSE:
                    this.taskStatusChange(record.getTaskCode(), TaskStatus.PAUSE);
                    break;
//                case TASK_RUNNING:
//                    this.taskStatusChange(record.getTaskCode(), TaskStatus.ONLINE);
//                    break;
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

    /**
     * @description  实时接收来自处理器的任务执行日志
     * @param riceLog
     * @throws
     */
    @Override
    public void appendLog(RiceLog riceLog) {
        repository.getRiceLogDao().append(riceLog);
    }

    private boolean taskIsRunning(String taskCode) {
        return clients.containsKey(taskCode) && clients.get(taskCode).isStarted();
    }

    /**
     * TODO
     * <p> 如果是调度器上线 -> 那么找到新的调度器所在的hash环位置的下一个node
     * 1 如果当前调度器集群只有一台机器 且当前的调度器就是上线的调度器，那么执行所有任务
     * 2 如果当前调度器集群超过1台，那么当前的调度器分三种情况
     * 2.1 currentNode = nextNode 需要将当前机器上的部分任务停止
     * 2.2 currentNode != nextNode && currentNode != changeNode  不需要做任何处理
     * 2.3 currentNode == changeNode  计算出nextNode上漂移到changeNode上的任务并启动
     * </>
     *
     * <p> 如果是调度器下线   剩余的调度器会瓜分该调度器上的任务
     * 1 查询该调度器的所有任务
     * 2 一致性hash算法 计算出能获取哪些任务 并启动任务实例
     * </>
     * <p>
     * 1.0.0版本对所有的任务做一致性hash
     */
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
                this.addTask(currentScheduler, infos.get(0), processores);
            }

        });
    }

    @Override
    public void isolationProcessorForAllTasks(String address) {
        clients.values().stream().filter(client -> {
            return client.getProcesses().stream().anyMatch(processor -> {
                return processor.contains(address);
            });
        }).forEach(client -> {
            client.isolationProcessor(address);
        });
    }

    public Set<String> getValidProcessorAddress() {
        Set<String> addresses = new HashSet<>();
        if (!clients.isEmpty()) {
            clients.values().forEach(client -> {
                addresses.addAll(client.getProcesses());
            });
        }
        return addresses;
    }

    // 全局心跳
    // 往执行器上面发送心跳  如果是第一次发送心跳，且发现无法连接上，那么修改db
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
                    processorHeartBeatRecord.put(address, System.currentTimeMillis());
                } catch (Exception e) {
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

    public Long procesorLatestHeartBeat(String address) {
        Long time = processorHeartBeatRecord.get(address);
        return time == null ? 0L : time;
    }

    public List<String> getManageTask() {
        return new ArrayList<>(clients.keySet());
    }

    public Repository getRepository() {
        return repository;
    }
}
