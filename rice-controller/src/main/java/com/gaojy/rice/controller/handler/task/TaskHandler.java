package com.gaojy.rice.controller.handler.task;

import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.controller.handler.PageSpec;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.RequestMapping;

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

    @RequestMapping(value = "/query",method = "GET")
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


    @RequestMapping(value = "/create",method = "POST")
    public HttpResponse create(HttpRequest request) {
        return null;
    }

    @RequestMapping(value = "/update",method = "POST")
    public HttpResponse update(HttpRequest request) {
        return null;
    }

    @RequestMapping(value = "/operation",method = "POST")
    public HttpResponse operation(HttpRequest request) {
        return null;
    }


}
