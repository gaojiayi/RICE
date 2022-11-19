package com.gaojy.rice.controller.handler;

import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.http.api.HttpBinder;
import com.gaojy.rice.http.api.HttpHandler;
import com.gaojy.rice.repository.api.Repository;

/**
 * @author gaojy
 * @ClassName AbstractHttpHandler.java
 * @Description
 * @createTime 2022/11/16 23:09:00
 */
public abstract class AbstractHttpHandler implements HttpHandler {
    public final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class)
        .getExtension("mysql");


    final String path;

    public AbstractHttpHandler(String path) {
        this.path = path;
    }


    public void registerHandler(HttpBinder binder) {
        binder.addHttpHandler(path, AbstractHttpHandler.this);
    }
}
