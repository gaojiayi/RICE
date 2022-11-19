package com.gaojy.rice.controller.handler;

import com.gaojy.rice.controller.handler.app.AppCreateHandler;
import com.gaojy.rice.controller.handler.task.TaskCreateHandler;
import com.gaojy.rice.http.api.HttpBinder;

/**
 * @author gaojy
 * @ClassName HandlerFactory.java
 * @Description
 * @createTime 2022/11/16 23:37:00
 */
public class HttpRouter {
    public static final String RICE_PATH = "/rice";
    public static final String HOME_PATH = RICE_PATH + "/home";
    public static final String TASK_PATH = RICE_PATH + "/task";
    public static final String APP_PATH = RICE_PATH + "/app";

    public static void addHandlers(HttpBinder binder) {
        addAppRouter(binder);
        addTaskRouter(binder);
    }

    private static void addAppRouter(HttpBinder binder) {
        // app
        new AppCreateHandler(APP_PATH + "/create").registerHandler(binder);
    }

    private static void addTaskRouter(HttpBinder binder) {
        // task
        new TaskCreateHandler(TASK_PATH + "/create").registerHandler(binder);
    }

    private static void addHomeRouter(HttpBinder binder) {
        new TaskCreateHandler(HOME_PATH + "/metrics").registerHandler(binder);

    }
}
