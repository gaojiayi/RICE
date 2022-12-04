package com.gaojy.rice.controller.handler.task;

import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.RequestMapping;

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

    @RequestMapping(value = "/query",method = "GET")
    public HttpResponse query(HttpRequest request) {
        return null;
    }

    @RequestMapping(value = "/log",method = "GET")
    public HttpResponse showLog(HttpRequest request) {
        return null;
    }
}
