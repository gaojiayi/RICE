package com.gaojy.rice.controller.handler.task;

import com.alibaba.fastjson.JSON;
import com.gaojy.rice.common.allocation.AllocationType;
import com.gaojy.rice.common.allocation.TaskAllocationFactory;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.constants.TaskOptType;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.protocol.body.controller.TaskCreateRequestBody;
import com.gaojy.rice.controller.RiceControllerBootStrap;
import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.controller.handler.PageSpec;
import com.gaojy.rice.controller.maintain.SchedulerManager;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.RequestMapping;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.channel.Channel;
import java.util.Date;

/**
 * @author gaojy
 * @ClassName TaskHandler.java
 * @Description
 * @createTime 2022/12/04 23:42:00
 */
public class TaskHandler extends AbstractHttpHandler {
    public TaskHandler(String rootPath) {
        super(rootPath);
    }

    @RequestMapping(value = "/query", method = "GET")
    public HttpResponse query(HttpRequest request) {
        // 应用ID  任务编码  分页
        String taskCode = (String) request.getParamMap().get("taskCode");
        Long appId = (Long) request.getParamMap().get("appId");
        Integer pageIndex = (Integer) request.getParamMap().get("pageIndex");
        Integer limit = (Integer) request.getParamMap().get("pageLimit");

        return new HttpResponse()
            .addResponse(
                "tasks", repository.getRiceTaskInfoDao().queryTasks(taskCode, appId, pageIndex, limit))
            .addResponse(
                "page", new PageSpec(pageIndex, limit,
                    repository.getRiceTaskInfoDao().queryTasksCount(taskCode, appId, pageIndex, limit)));
    }

    @RequestMapping(value = "/create", method = "POST")
    public HttpResponse create(HttpRequest request) {
        String dataJson = JSON.toJSONString(request.getParamMap());
        RiceTaskInfo riceTaskInfo = JSON.parseObject(dataJson, RiceTaskInfo.class);
        riceTaskInfo.setStatus(1);
        riceTaskInfo.setCreateTime(new Date());
        repository.getRiceTaskInfoDao().addTask(riceTaskInfo);
        // TODO：通知调度器
        String schdulerServer = TaskAllocationFactory.getStrategy(AllocationType.CONSISTENTHASH).allocateServer(
            SchedulerManager.getManager().getActiveSchedulerAddr(), riceTaskInfo.getTaskCode());
        Channel channel = SchedulerManager.getManager().getChannel(schdulerServer);
        RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.CONTROLLER_TASK_CREATE, null);
        TaskCreateRequestBody body = new TaskCreateRequestBody();
        body.setTaskInfo(riceTaskInfo);
        requestCommand.setBody(body.encode());
        try {
            RiceRemoteContext response = RiceControllerBootStrap.getController()
                .getRemotingServer().invokeSync(channel, requestCommand, 3000);
            if(response.getCode() == ResponseCode.SUCCESS){
                log.info("success");
                return null;
            }
        } catch (Exception  e) {
            log.error(e.getMessage());
        }

        log.error("");
        return new HttpResponse(500,"create task failed");
    }

    @RequestMapping(value = "/update", method = "POST")
    public HttpResponse update(HttpRequest request) {
        RiceTaskInfo taskInfo = repository.getRiceTaskInfoDao().getInfoByCode((String) request.getParamMap().get("taskCode"));
        if (taskInfo == null) {
            return new HttpResponse(500, "task not exist");
        }
        if (request.getParamMap().get("taskName") != null) {
            taskInfo.setTaskName((String) request.getParamMap().get("taskName"));
        }
        repository.getRiceTaskInfoDao().updateTask(taskInfo);
        return null;
    }

    /**
     * @description 暂停  启动   立即执行
     */
    @RequestMapping(value = "/operation", method = "POST")
    public HttpResponse operation(HttpRequest request) {
        Integer opt = (Integer) request.getParamMap().get("optType");
        String optDesc = TaskOptType.getTaskOptType(opt).getName();
        TaskChangeRecord record = new TaskChangeRecord();
        record.setTaskCode((String) request.getParamMap().get("taskCode"));
        record.setOptType(opt);
        record.setCreateTime(new Date());
        record.setOptDesc(optDesc);
        repository.getRiceTaskChangeRecordDao().insert(record);
        return null;
    }

}
