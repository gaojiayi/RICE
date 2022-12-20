package com.gaojy.rice.controller.handler;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.http.api.HttpBinder;
import com.gaojy.rice.http.api.HttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.RequestMapping;
import com.gaojy.rice.repository.api.Repository;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName AbstractHttpHandler.java
 * @Description
 * @createTime 2022/11/16 23:09:00
 */
public abstract class AbstractHttpHandler implements HttpHandler {
    public static final Logger log = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);
    private static final Map<String, Method> routerMethods = new HashMap<>();
    public final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class)
        .getExtension("mysql");

    final String rootPath;

    public AbstractHttpHandler(String rootPath) {
        this.rootPath = rootPath;
    }

    public void registerHandler(HttpBinder binder) {
        // 找到当前handler中所有的url   再依次注册
        Method[] methods = this.getClass().getMethods();
        Arrays.stream(methods).forEach(method -> {
            if (method.getAnnotation(RequestMapping.class) != null) {
                RequestMapping rm = method.getAnnotation(RequestMapping.class);
                Method preMethod = routerMethods.putIfAbsent(rootPath + rm.value(), method);
                if (preMethod != null) {
                    throw new RuntimeException("Exist same url," + rm.value());
                }
                binder.addHttpHandler(rootPath + rm.value(), AbstractHttpHandler.this);
            }

        });

    }

    @Override
    public HttpResponse handler(HttpRequest request, String url, String httpMethod) throws Exception {
        Method method = routerMethods.get(url);
        if (method == null || !method.getAnnotation(RequestMapping.class).method()
            .equalsIgnoreCase(httpMethod)) {
            log.error("Not found Router,please check url or method. url=" + url);
            return new HttpResponse(404, "Not found Router");
        }
        Object ret = method.invoke(this, request);
        if (ret == null)
            return new HttpResponse(202, "");
        if (ret instanceof HttpResponse) {
            return (HttpResponse) ret;
        }
        return new HttpResponse(500, "Need return HttpResponse type");
    }
}
