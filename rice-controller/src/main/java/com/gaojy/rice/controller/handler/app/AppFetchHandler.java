package com.gaojy.rice.controller.handler.app;

import com.gaojy.rice.common.entity.RiceAppInfo;
import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName AppFetchHandler.java
 * @Description
 * @createTime 2022/11/20 22:02:00
 */
public class AppFetchHandler extends AbstractHttpHandler {
    public AppFetchHandler(String path) {
        super(path);
    }

    @Override
    public HttpResponse handler(HttpRequest request) throws Exception {
        Map<String, List<RiceAppInfo>> responseMap = new HashMap<>();

        String appName = (String) request.getParamMap().get("appName");
        Integer pageIndex = (Integer) request.getParamMap().get("pageIndex");
        Integer limit = (Integer) request.getParamMap().get("pageLimit");
        responseMap.put("data", repository.getRiceAppInfoDao().queryApps(appName, pageIndex, limit));
        return new HttpResponse(responseMap);
    }
}
