package com.gaojy.rice.dispatcher.scheduler;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.ExecuteType;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.ScheduleType;
import com.gaojy.rice.common.constants.TaskSataus;
import com.gaojy.rice.common.constants.TaskType;
import com.gaojy.rice.common.timewheel.HashedWheelTimer;
import com.gaojy.rice.common.timewheel.Timeout;
import com.gaojy.rice.common.timewheel.TimerTask;
import com.gaojy.rice.dispatcher.common.DispatcherAPIWrapper;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TaskScheduleClient.java
 * @Description 任务调度单位
 * @createTime 2022/02/11 11:12:00
 */
public class TaskScheduleClient implements TimerTask {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);
    // 所有的调度使用一个时间轮
    private final HashedWheelTimer scheduleTimer;
    private final String taskCode;
    private final String taskName;
    private TaskType taskType;
    private String parameters;
    private ScheduleType scheduleType = ScheduleType.CRON;
    private String timeExpression;
    private ExecuteType executeType;
    private int executeThreads;
    private int taskRetryCount = 0;
    private int instanceRetryCount = 0;
    private CronExpression cexpStart;
    private Set<String> processes = Collections.synchronizedSet(new HashSet<>());
    private AtomicReference<TaskSataus> taskSataus = new AtomicReference<>();
    private DispatcherAPIWrapper outApiWrapper;

    public TaskScheduleClient(String taskCode, String taskName, HashedWheelTimer scheduleTimer,
        DispatcherAPIWrapper outApiWrapper) throws ParseException {
        this.taskCode = taskCode;
        this.taskName = taskName;
        this.scheduleTimer = scheduleTimer;
        this.outApiWrapper = outApiWrapper;
        if (ScheduleType.CRON.equals(scheduleType)) {
            cexpStart = new CronExpression(timeExpression);
        }

        // 把处理器增加到监听
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (taskSataus.get().equals(TaskSataus.OFFLINE)) {
            return;
        }

        if (taskSataus.get().equals(TaskSataus.ONLINE)) {
            // todo:真正需要实现的逻辑

            if (ExecuteType.STANDALONE.equals(executeType)) { // 单机执行
                // 负载均衡  找到一个处理器
            }

        } else if (taskSataus.get().equals(TaskSataus.PAUSE)) { //跳过本次执行
            log.info("taskCode={},status is pause", taskCode);
        }
        // 计算下次执行
        long delay = 0L;
        if (ScheduleType.CRON.equals(scheduleType)) {
            Date current = new Date();
            Date firstStartTime = cexpStart.getNextValidTimeAfter(current);
            delay = firstStartTime.getTime() - current.getTime();
        }
        if (ScheduleType.FIX_RATE.equals(scheduleType)) {
            delay = Long.parseLong(timeExpression);
        }
        // 固定延迟  则在回调中执行

        scheduleTimer.newTimeout(this, delay, TimeUnit.MILLISECONDS);
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaskScheduleClient))
            return false;
        TaskScheduleClient client = (TaskScheduleClient) o;
        return taskCode.equals(client.taskCode);
    }

    @Override public int hashCode() {
        return Objects.hash(taskCode);
    }

    public Set<String> getProcesses() {
        return processes;
    }
}
