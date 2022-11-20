package com.gaojy.rice.controller.handler.home;

import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import java.util.HashMap;
import java.util.List;

/**
 * @author gaojy
 * @ClassName HomeScrollBarHandler.java
 * @Description
 * @createTime 2022/11/19 22:56:00
 */
public class HomeScrollBarHandler extends AbstractHttpHandler {

    public HomeScrollBarHandler(String path) {
        super(path);
    }

    @Override
    public HttpResponse handler(HttpRequest request) throws Exception {
        HashMap<String, List<TaskInstanceInfo>> responseMap = new HashMap<>();
        Object limit = request.getParamMap().get("limit");
        if (limit == null) {
            limit = 10;
        }
        List<TaskInstanceInfo> latestInstances = repository.getTaskInstanceInfoDao()
            .getLatestInstance(Integer.parseInt(limit.toString()));
        responseMap.put("data", latestInstances);
        return new HttpResponse(responseMap);
    }
}
