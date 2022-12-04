package com.gaojy.rice.controller.handler.task;

import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.http.api.HttpBinder;
import com.gaojy.rice.http.api.HttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.HttpServer;

/**
 * 任务新增handler    将任务做一致性hash之后确定分配在哪个调度器，并请求调取器添加任务
 */
public class TaskCreateHandler extends AbstractHttpHandler  {

    public TaskCreateHandler(String path) {
        super(path);
    }

    public HttpResponse handler(HttpRequest request) throws Exception {
        // 获取参数

        // 插入db

        // 一致性hash  获取 调度器地址

        // 通知调度器 任务创建
        return null;
    }

}
