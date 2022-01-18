package com.gaojy.rice.http.api;

import com.gaojy.rice.common.extension.SPI;

import java.net.InetSocketAddress;

public interface HttpServer {



    /**
     * close the channel.
     */
    void close();

    /**
     * is closed.
     *
     * @return closed
     */
    boolean isClosed();

}