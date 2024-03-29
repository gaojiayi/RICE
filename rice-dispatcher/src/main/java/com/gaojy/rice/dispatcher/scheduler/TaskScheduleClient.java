package com.gaojy.rice.dispatcher.scheduler;

import com.alipay.remoting.LifeCycleException;
import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.balance.Balance;
import com.gaojy.rice.common.balance.RandomBalance;
import com.gaojy.rice.common.constants.*;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.common.exception.*;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.common.protocol.body.scheduler.TaskInvokerResponseBody;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokeRequestHeader;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokerResponseHeader;
import com.gaojy.rice.common.timewheel.HashedWheelTimer;
import com.gaojy.rice.common.timewheel.Timeout;
import com.gaojy.rice.common.timewheel.TimerTask;
import com.gaojy.rice.dispatcher.common.DispatcherAPIWrapper;
import com.gaojy.rice.dispatcher.longpolling.PullRequest;
import com.gaojy.rice.dispatcher.longpolling.PullTaskService;
import com.gaojy.rice.dispatcher.scheduler.tasktype.RiceExecuter;
import com.gaojy.rice.dispatcher.scheduler.tasktype.TaskExecuterFactory;
import com.gaojy.rice.remote.InvokeCallback;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.ResponseFuture;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.gaojy.rice.repository.api.Repository;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TaskScheduleClient.java
 * @Description 任务调度单位
 * @createTime 2022/02/11 11:12:00
 */
public class TaskScheduleClient implements TimerTask, LifeCycle {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);
    // 所有的调度使用一个时间轮
    private final HashedWheelTimer scheduleTimer;
    private String taskCode;
    private String taskName;
    private String taskDesc;
    private TaskType taskType;
    private String parameters;
    private ScheduleType scheduleType;
    private Long timeOutSec;
    private String timeExpression;
    private ExecuteType executeType;
    //    private int executeThreads = 1;
    private int taskRetryCount = 1;
    private int instanceRetryCount = 1;
    private CronExpression cexpStart;
    private volatile Set<String> processes = new CopyOnWriteArraySet();
    private AtomicReference<TaskStatus> taskStatus = new AtomicReference<>(TaskStatus.ONLINE);
    private DispatcherAPIWrapper outApiWrapper;

    private static ExecutorService fixedDelayScheduleThreadPool = Executors.newFixedThreadPool(
        16 + Runtime.getRuntime().availableProcessors() * 2,
        new RiceThreadFactory("fix_delay_schedule_thread"));
    ;
    // 任务启动时间
    private Long bootTime = System.currentTimeMillis();
    private PullTaskService pullTaskService;
    private TaskScheduleManager taskScheduleManager;
    private Balance balance = new RandomBalance();
    private final RiceExecuter riceExecuter;
    private final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class)
        .getExtension("mysql");

    private Long nextTaskInstanceId = -1L;

    private String appId;

    private Long timeoutMillis = 5 * 1000 * 60L;

    public TaskScheduleClient(RiceTaskInfo taskInfo,
        TaskScheduleManager taskScheduleManager) throws ParseException {
        this.taskScheduleManager = taskScheduleManager;
        buildClient(taskInfo);
        this.scheduleTimer = taskScheduleManager.getScheduleTimer();
        this.outApiWrapper = taskScheduleManager.getOutApiWrapper();
        this.pullTaskService = taskScheduleManager.getPullTaskService();
        if (ScheduleType.CRON.equals(scheduleType)) {
            cexpStart = new CronExpression(timeExpression);
        }
        riceExecuter = TaskExecuterFactory.getExecuter(taskType, this);
    }

    private void buildClient(RiceTaskInfo taskInfo) {
        try {
            BeanUtils.copyProperties(this, taskInfo);
            this.executeType = ExecuteType.getType(taskInfo.getExecuteType());
            this.taskType = TaskType.getType(taskInfo.getTaskType());
            this.scheduleType = ScheduleType.getType(taskInfo.getScheduleType());
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("build client error", e);
            throw new DispatcherException(e);
        }
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (taskStatus.get().equals(TaskStatus.OFFLINE)) {
            taskScheduleManager.taskStatusChange(taskCode, TaskStatus.OFFLINE);
            return;
        }

        // 不同的任务类型  具体的执行步骤又是不一样的
        if (taskStatus.get().equals(TaskStatus.ONLINE)) {
            // 更新任务实例的实际开始时间   已经状态
            TaskInstanceInfo taskInstance = repository.getTaskInstanceInfoDao().getInstance(nextTaskInstanceId);
            taskInstance.setActualTriggerTime(new Date());
            taskInstance.setStatus(TaskInstanceStatus.RUNNING.getCode());
            repository.getTaskInstanceInfoDao().updateTaskInstance(taskInstance);

            riceExecuter.execute(nextTaskInstanceId);

        } else if (taskStatus.get().equals(TaskStatus.PAUSE)) { //跳过本次执行
            log.info("taskCode={},status is pause", taskCode);
        }

        // 计算下次执行
        long delay = 0L;
        if (ScheduleType.CRON.equals(scheduleType)) {
            Date current = new Date();
            Date firstStartTime = cexpStart.getNextValidTimeAfter(current);
            delay = firstStartTime.getTime() - current.getTime();
        }
        // TODO 固定延迟是上一次任务执行完后延迟N 再执行（同步调用）    固定评率是每隔多长时间再执行 （异步调用）
        if (ScheduleType.FIXED_FREQUENCY.equals(scheduleType) || ScheduleType.FIX_DELAY.equals(scheduleType)) {
            delay = Long.parseLong(timeExpression) * 1000;
        }

        if (taskStatus.get().equals(TaskStatus.ONLINE)) {
            nextTaskInstanceId = repository.getTaskInstanceInfoDao().createTaskInstance(buildNewTaskInstance(delay));

        }
        triggerTask(delay);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaskScheduleClient))
            return false;
        TaskScheduleClient client = (TaskScheduleClient) o;
        return taskCode.equals(client.taskCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskCode);
    }

    public Set<String> getProcesses() {
        return processes;
    }

    @Override
    public void startup() throws LifeCycleException {
        this.pullTaskService.executePullRequestImmediately(new PullRequest(this.bootTime, taskCode));
        Long delay = 0L;
        // TODO 查询下一次触发时间 如果
        if (ScheduleType.FIX_DELAY.equals(scheduleType)) {
            // 固定延迟  则延迟调度
            delay = Long.parseLong(timeExpression);
        }

        nextTaskInstanceId = repository.getTaskInstanceInfoDao().createTaskInstance(buildNewTaskInstance(delay));
        triggerTask(delay);
    }

    private void triggerTask(long delay) {
        try {
            repository.getRiceTaskInfoDao().updateNextTriggerTime(this.taskCode, new Date(System.currentTimeMillis() + delay));
        } catch (Exception e) {
            log.error("update next Trigger Time error ,taskcode={},triggerTime={}",
                this.taskCode, new Date(System.currentTimeMillis() + delay));
        } finally {
            this.scheduleTimer.newTimeout(this, delay, TimeUnit.MILLISECONDS);
        }

    }

    public TaskInstanceInfo buildNewTaskInstance(Long delay) {
        TaskInstanceInfo info = new TaskInstanceInfo();
        info.setStatus(TaskInstanceStatus.WAIT.getCode());
        info.setCreateTime(new Date());
        info.setTaskCode(this.taskCode);
        info.setExpectedTriggerTime(new Date(delay + System.currentTimeMillis()));
        info.setInstanceParams(this.parameters);
        info.setType(taskType.name());
        info.setRetryTimes(0);
        return info;
    }

    @Override
    public void shutdown() throws LifeCycleException {
        taskStatus.set(TaskStatus.OFFLINE);
        fixedDelayScheduleThreadPool.shutdown();
    }

    @Override
    public boolean isStarted() {
        return taskStatus.get() != TaskStatus.OFFLINE;
    }

    public ExecutorService getThreadPool() {
        return fixedDelayScheduleThreadPool;
    }

    public List<String> selectProcessores(ExecuteType type) {
        List<String> selectRet = new ArrayList<>();
        type = executeType == null ? this.executeType : type;
        if (CollectionUtils.isNotEmpty(processes)) {
            List<String> all = processes.stream().collect(Collectors.toList());
            if (ExecuteType.STANDALONE.equals(executeType)) { // 单机执行
                // 负载均衡  找到一个处理器
                selectRet.add(balance.select(all));
            }
            if (ExecuteType.BROADCAST.equals(executeType)) { // 广播执行
                selectRet.addAll(all);
            }
        }
        return selectRet;
    }

    public String selectOneProcessor() {
        if (CollectionUtils.isNotEmpty(processes)) {
            List<String> all = processes.stream().collect(Collectors.toList());
            return balance.select(all);
        }
        return null;
    }

    public void invokeAsync(String processorAddr, final RiceRemoteContext remoteContext)
        throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
        InterruptedException, RemotingTooMuchRequestException {
        InvokeCallback callback = new InvokeCallback() {

            @Override
            public void operationComplete(ResponseFuture responseFuture) {
                RiceRemoteContext response = responseFuture.getResponseCommand();
                // 更新数据库
                Long taskInstanceId = ((TaskInvokeRequestHeader) remoteContext.readCustomHeader()).getTaskInstanceId();
                TaskInstanceInfo instanceInfo = repository.getTaskInstanceInfoDao().getInstance(taskInstanceId);
                instanceInfo.setStatus(TaskInstanceStatus.FINISHED.getCode());
                instanceInfo.setFinishedTime(new Date());
                instanceInfo.setTaskTrackerAddress(processorAddr);
                if (response != null) {
                    try {
                        TaskInvokerResponseHeader responseHeader = (TaskInvokerResponseHeader) response
                            .decodeCommandCustomHeader(TaskInvokerResponseHeader.class);
                        TaskInvokerResponseBody body = TaskInvokerResponseBody.decode(response.getBody(),
                            TaskInvokerResponseBody.class);
                        // 更新实例状态
                        instanceInfo.setResult(body.toJson());
                        instanceInfo.setFinishedTime(new Date(responseHeader.getFinishTime()));
                        instanceInfo.setRetryTimes(responseHeader.getRetryTimes());
                    } catch (RemotingCommandException e) {
                        instanceInfo.setStatus(TaskInstanceStatus.EXCEPTION.getCode());
                    }

                } else {
                    if (responseFuture.isSendRequestOK()) {
                        instanceInfo.setStatus(TaskInstanceStatus.EXCEPTION.getCode());
                    } else if (responseFuture.isTimeout()) {
                        instanceInfo.setStatus(TaskInstanceStatus.TIMEOUT.getCode());
                    } else {
                        if (responseFuture.getCause() != null) {
                            instanceInfo.setStatus(TaskInstanceStatus.EXCEPTION.getCode());
                        }
                    }
                }

                repository.getTaskInstanceInfoDao().updateTaskInstance(instanceInfo);
            }
        };
        outApiWrapper.invokeTask(processorAddr, remoteContext, timeoutMillis, callback, false);
    }

    public RiceRemoteContext invokeSync(String processorAddr, final RiceRemoteContext remoteContext)
        throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
        InterruptedException, RemotingTooMuchRequestException {
        return outApiWrapper.invokeTask(processorAddr, remoteContext, timeoutMillis, null, true);
    }

    public void initProcessores(List<ProcessorServerInfo> list) {
        if (list != null && list.size() > 0) {
            list.forEach(processor -> {
                processes.add(processor.getAddress() + ":" + processor.getPort());
            });
        }
    }

    public AtomicReference<TaskStatus> getTaskStatus() {
        return taskStatus;
    }

    public void setProcesses(Set<String> processes) {
        this.processes = processes;
    }

    public void isolationProcessor(String ip) {
        processes.removeAll(processes.stream().filter(server -> {
            return server.contains(ip);
        }).collect(Collectors.toList()));
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public ExecuteType getExecuteType() {
        return executeType;
    }

    public void setExecuteType(String executeType) {
        this.executeType = ExecuteType.getType(executeType);
    }

    public int getTaskRetryCount() {
        return taskRetryCount;
    }

    public void setTaskRetryCount(int taskRetryCount) {
        this.taskRetryCount = taskRetryCount;
    }

    public void setTaskType(int taskType) {
        this.taskType = TaskType.getType(taskType);

    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public int getInstanceRetryCount() {
        return instanceRetryCount;
    }

    public void setInstanceRetryCount(int instanceRetryCount) {
        this.instanceRetryCount = instanceRetryCount;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = ScheduleType.getType(scheduleType);
    }

    public Repository getRepository() {
        return repository;
    }

    public Long getTimeOutSec() {
        return timeOutSec;
    }

    public void setTimeOutSec(Long timeOutSec) {
        this.timeOutSec = timeOutSec;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTimeExpression() {
        return timeExpression;
    }

    public void setTimeExpression(String timeExpression) {
        this.timeExpression = timeExpression;
    }
}
