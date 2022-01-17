package com.gaojy.rice.http.api;

import com.gaojy.rice.common.extension.SPI;

/**
 * @author gaojy
 * @ClassName HttpPostHandler.java
 * @Description TODO
 * @createTime 2022/01/18 00:20:00
 */
@SPI
public interface HttpPostHandler extends HttpHandler {
    public HttpResponse handler(HttpRequest request);
}
