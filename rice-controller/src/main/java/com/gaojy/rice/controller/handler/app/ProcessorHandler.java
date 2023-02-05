package com.gaojy.rice.controller.handler.app;

import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.entity.RiceAppInfo;
import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.RequestMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gaojy
 * @ClassName ProcessorHandler.java
 * @Description
 * @createTime 2022/12/05 00:27:00
 */
public class ProcessorHandler extends AbstractHttpHandler {
    public ProcessorHandler(String rootPath) {
        super(rootPath);
    }

    @RequestMapping(value = "/info", method = "GET")
    public HttpResponse processorInfo(HttpRequest request) throws Exception {
        Long appId = null;
        if (request.getParamMap().get("appId") != null) {
            appId = Long.valueOf((String) request.getParamMap().get("appId"));
        }

        // 暂时不考虑分页
        Integer pageIndex = Integer.parseInt((String) request.getParamMap().get("pageIndex"));
        Integer limit = Integer.parseInt((String) request.getParamMap().get("pageSize"));
        // 查询所有的处理器
        // 查询处理器的id
        List<ProcessorServerInfo> processorServerInfos = repository.getProcessorServerInfoDao().queryProcessorServers(appId);
        Map<String, List<ProcessorServerInfo>> processorServerMap = processorServerInfos.stream().collect(Collectors.groupingBy(p -> {
            return p.getAddress() + ":" + p.getPort();
        }));
        List<Long> appIds = processorServerInfos.stream().map(ProcessorServerInfo::getAppId).distinct().collect(Collectors.toList());
        List<RiceAppInfo> riceAppInfos = repository.getRiceAppInfoDao().queryAppsByIds(appIds);
        Map<Long, List<RiceAppInfo>> collectApps = riceAppInfos.stream().collect(Collectors.groupingBy(RiceAppInfo::getId));

        List<Map<String, Object>> retList = new ArrayList<>();
        processorServerMap.forEach((address, pList) -> {
            Map<String, Object> processor = new HashMap<>();
            if (pList.size() > 0) {
                String appName = collectApps.get(pList.get(0).getAppId()).get(0).getAppName();
                processor.put("app_name", appName);
                List<String> tasks = pList.stream().map(ProcessorServerInfo::getTaskCode).collect(Collectors.toList());
                processor.put("tasks", tasks);
            }
            processor.put("address", address.split(":")[0]);
            processor.put("port", address.split(":")[1]);
            processor.put("server_status", 1);
            retList.add(processor);
        });
        return new HttpResponse("list", retList);
    }
}
