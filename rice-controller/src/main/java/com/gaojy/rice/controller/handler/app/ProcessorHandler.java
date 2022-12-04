package com.gaojy.rice.controller.handler.app;

import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.RequestMapping;

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

        return null;
    }
}
