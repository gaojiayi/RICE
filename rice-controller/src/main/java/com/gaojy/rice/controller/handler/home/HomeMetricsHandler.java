package com.gaojy.rice.controller.handler.home;

import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.controller.maintain.SchedulerManager;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName HomeMetricsHandler.java
 * @Description
 * @createTime 2022/11/18 22:24:00
 */
public class HomeMetricsHandler extends AbstractHttpHandler {

    public HomeMetricsHandler(String path) {
        super(path);
    }

    @Override
    public HttpResponse handler(HttpRequest request) throws Exception {
        Map<String, Integer> responseMap = new HashMap<>();
        List<String> allValidTaskCode = repository.getRiceTaskInfoDao().getAllValidTaskCode();
        Integer countValidInstance = repository.getTaskInstanceInfoDao().getCountValidInstance();
        int schedulerServerNum = SchedulerManager.getManager().getActiveScheduler().size();
        responseMap.put("task_num", allValidTaskCode.size());
        responseMap.put("total_schedule_times", countValidInstance);
        responseMap.put("scheduler_num", schedulerServerNum);
        return new HttpResponse(responseMap);
    }
}
