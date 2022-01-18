package com.gaojy.rice.http.netty;

import com.gaojy.rice.http.api.HttpServer;


public class NettyServer implements HttpServer {

    @Override
    public void close() {

    }


    @Override
    public boolean isClosed() {
        return false;
    }
}
