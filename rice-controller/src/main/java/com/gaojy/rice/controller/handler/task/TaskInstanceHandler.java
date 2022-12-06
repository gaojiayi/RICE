package com.gaojy.rice.controller.handler.task;

import com.gaojy.rice.common.entity.RiceLog;
import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.RequestMapping;
import java.util.Date;
import java.util.List;

/**
 * @author gaojy
 * @ClassName TaskInstanceHandler.java
 * @Description
 * @createTime 2022/12/05 00:29:00
 */
public class TaskInstanceHandler extends AbstractHttpHandler {
    public TaskInstanceHandler(String rootPath) {
        super(rootPath);
    }

    @RequestMapping(value = "/query", method = "GET")
    public HttpResponse query(HttpRequest request) {
        return null;
    }

    @RequestMapping(value = "/logs", method = "GET")
    public HttpResponse showLog(HttpRequest request) {
        Object time = request.getParamMap().get("time");
        Date startTime = new Date(0L);
        if (time != null) {
            startTime = new Date((Long) time);
        }
        Long taskInstanceId = (Long) request.getParamMap().get("taskInstanceId");
        List<RiceLog> riceLogs = repository.getRiceLogDao().printLog(taskInstanceId, startTime);
        return new HttpResponse("logs", riceLogs);
    }
}
