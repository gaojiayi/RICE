package com.gaojy.rice.http.jetty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.http.api.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetty 服务器构建器
 */
public class JettyHttpBinder implements HttpBinder {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.HTTP_AGENT_LOGGER_NAME);
    private final int DEFAULT_THREADS = 200;
    private final String ROOT_PATH = "/rice";
    private final Server server;
    private Map<String, HttpHandler> handlers = new HashMap<>();
    private final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

    public JettyHttpBinder() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setDaemon(true);
        threadPool.setMaxThreads(DEFAULT_THREADS);
        threadPool.setMinThreads(DEFAULT_THREADS - 100);
        server = new Server(threadPool);
    }

    @Override
    public void addHttpHandler(String path, HttpHandler handler) {
        handlers.put(path, handler);
        contextHandler.addServlet(new ServletHolder(new JettyHttpServlet(path)), path);

    }

    @Override
    public HttpServer bind(int port) {
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);
        contextHandler.setContextPath(ROOT_PATH);
        server.setHandler(contextHandler);
        return new JettyServer(server);

    }

    class JettyHttpServlet extends HttpServlet {
        final String path;

        public JettyHttpServlet(String path) {
            this.path = path;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            Map<String, Object> paramMap = new HashMap<>();
            HttpRequest request = new HttpRequest(paramMap);

            if (null != req.getContentType() && req.getContentType().toLowerCase().indexOf("json") > 0) {
                String body = req.getReader().readLine();
                if (StringUtil.isNotBlank(body)) {
                    JSONObject jsonBody = JSON.parseObject(body);
                    jsonBody.forEach((k, v) -> {
                        if (v != null) {
                            paramMap.put(k, v);
                        }
                    });
                }
            } else {
                req.getParameterMap().forEach((k, v) -> {
                    if (v != null && v.length > 0) {
                        paramMap.put(k, String.valueOf(v[0]));
                    }
                });
            }
            HttpHandler h = JettyHttpBinder.this.handlers.get(this.path);
            HttpResponse response = null;
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            try {
                response = h.handler(request, req.getRequestURI(),req.getMethod());
                resp.setStatus(response.getRespCode());

                resp.getWriter().print(response.toJsonString());

            } catch (Exception e) {
                log.error("Api = {}, request param = {} , occur error:{},", this.path, paramMap, e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }

        }
    }
}
