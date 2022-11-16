package com.gaojy.rice.controller.handler;

import com.gaojy.rice.http.api.HttpBinder;
import com.gaojy.rice.http.api.HttpHandler;

/**
 * @author gaojy
 * @ClassName AbstractHttpHandler.java
 * @Description
 * @createTime 2022/11/16 23:09:00
 */
public abstract class AbstractHttpHandler implements HttpHandler {

    final String path;

    public AbstractHttpHandler(String path) {
        this.path = path;
    }


    public void registerHandler(HttpBinder binder) {
        binder.addHttpHandler(path, AbstractHttpHandler.this);
    }
}
