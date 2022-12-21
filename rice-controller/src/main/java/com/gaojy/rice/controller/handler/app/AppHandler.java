package com.gaojy.rice.controller.handler.app;

import com.gaojy.rice.common.entity.RiceAppInfo;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.controller.handler.PageSpec;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.RequestMapping;
import java.util.Date;

/**
 * @author gaojy
 * @ClassName AppHandler.java
 * @Description 应用管理
 * @createTime 2022/12/04 23:28:00
 */
public class AppHandler extends AbstractHttpHandler {
    public AppHandler(String rootPath) {
        super(rootPath);
    }

    @RequestMapping(value = "/create", method = "POST")
    public HttpResponse create(HttpRequest request) throws Exception {
        try {
            String appName = (String) request.getParamMap().get("appName");
            String appDesc = (String) request.getParamMap().get("appDesc");
            if (StringUtil.isEmpty(appName)) {
                return new HttpResponse(400, "param error,missing appName");
            }
            RiceAppInfo appInfo = new RiceAppInfo();
            appInfo.setAppName(appName);
            appInfo.setAppDesc(appDesc);
            appInfo.setCreateTime(new Date());
            appInfo.setStatus(1);
            repository.getRiceAppInfoDao().createApp(appInfo);
            return new HttpResponse();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw e;
        }
    }

    @RequestMapping(value = "/delete", method = "POST")
    public HttpResponse delete(HttpRequest request) throws Exception {
        try {
            String appId = String.valueOf(request.getParamMap().get("appId"));
            if (StringUtil.isEmpty(appId)) {
                return new HttpResponse(400, "param error,missing appId");
            }
            repository.getRiceAppInfoDao().deleteAppById(Long.valueOf(appId));
            return new HttpResponse();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw e;
        }
    }

    @RequestMapping(value = "/fetch", method = "GET")
    public HttpResponse fetch(HttpRequest request) throws Exception {

        String appName = (String) request.getParamMap().get("appName");
        Integer pageIndex = Integer.parseInt((String) request.getParamMap().get("pageIndex"));
        Integer limit = Integer.parseInt((String) request.getParamMap().get("pageLimit"));
        return new HttpResponse()
            .addResponse(
                "appList",
                repository.getRiceAppInfoDao().queryApps(appName, pageIndex, limit))
            .addResponse("page", new PageSpec(pageIndex, limit,
                repository.getRiceAppInfoDao().queryAppsCount(appName)));
    }

}
