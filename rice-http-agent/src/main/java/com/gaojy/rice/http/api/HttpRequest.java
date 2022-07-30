package com.gaojy.rice.http.api;

import java.util.Map;

/**
 * @author gaojy
 * @ClassName HttpRequest.java
 * @Description
 * @createTime 2022/01/18 00:05:00
 */
public class HttpRequest {

    private Map<String, Object> paramMap;

    public HttpRequest(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }
}
