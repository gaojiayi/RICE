package com.gaojy.rice.http.api;

import com.gaojy.rice.common.extension.SPI;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author gaojy
 * @ClassName HttpBinder.java
 * @Description TODO
 * @createTime 2022/01/17 20:50:00
 */
@SPI
public interface HttpBinder {

    void addHttpHandler(String path,  HttpHandler handler);

    HttpServer bind(int port);

}
