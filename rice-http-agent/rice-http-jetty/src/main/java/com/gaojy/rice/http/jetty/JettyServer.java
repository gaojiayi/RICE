package com.gaojy.rice.http.jetty;

import com.gaojy.rice.http.api.HttpBinder;
import com.gaojy.rice.http.api.HttpHandler;
import com.gaojy.rice.http.api.HttpServer;
import java.net.InetSocketAddress;

/**
 * @author gaojy
 * @ClassName JettyServer.java
 * @Description TODO
 * @createTime 2022/01/18 00:21:00
 */
public class JettyServer implements HttpServer, HttpBinder {

    @Override public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override public void close() {

    }

    @Override public void close(int timeout) {

    }

    @Override public boolean isBound() {
        return false;
    }

    @Override public boolean isClosed() {
        return false;
    }

    @Override public void addHttpHandler(String path, HttpHandler handler) {

    }

    @Override public HttpServer bind(int port) {
        return null;
    }
}
