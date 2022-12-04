package com.gaojy.rice.http.api;

/**
 * @author gaojy
 * @ClassName HttpHandler.java
 * @Description 
 * @createTime 2022/01/18 00:24:00
 */
public interface HttpHandler {
    public HttpResponse handler(HttpRequest request,String url,String method) throws Exception;
}
