package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.alibaba.fastjson.JSONArray;
import com.gaojy.rice.common.constants.ExecuterMethodName;
import com.gaojy.rice.common.constants.TaskInstanceStatus;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaojy
 * @ClassName RiceMapReduceTaskExecuter.java
 * @Description map reduce 任务执行器
 * @createTime 2022/02/14 13:55:00
 */
public class RiceMapReduceTaskExecuter extends RiceMapTaskExecuter {
    public RiceMapReduceTaskExecuter(TaskScheduleClient client) {
        super(client);
    }

    @Override
    public void execute(Long taskInstanceId) {
        // 先执行map
        // 后执行process
        super.execute(taskInstanceId);
        List<TaskInstanceInfo> instances = repository.getTaskInstanceInfoDao().getInstances(ExecuterMethodName.process.name(), taskInstanceId);
        //  查询数据库获取结果   再执行reduce
        List<String> allResult = instances.stream().map(TaskInstanceInfo::getResult).collect(Collectors.toList());
        TaskInstanceInfo info = buildReduceTaskInstance(taskInstanceId, JSONArray.toJSONString(allResult));
        client.getThreadPool().submit(new StandaloneTaskRunnable(info));

    }

    public TaskInstanceInfo buildReduceTaskInstance(long parentInstanceId,String param){
        TaskInstanceInfo info = new TaskInstanceInfo();
        info.setType(ExecuterMethodName.reduce.name());
        info.setParentInstanceId(parentInstanceId);
        info.setInstanceParams(param);
        info.setTaskCode(client.getTaskCode());
        info.setStatus(TaskInstanceStatus.WAIT.getCode());
        info.setCreateTime(new Date());
        info.setExpectedTriggerTime(new Date());
        info.setActualTriggerTime(new Date());
        return info;
    }
}
