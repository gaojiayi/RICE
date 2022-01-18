package com.gaojy.rice.http.api;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName HttpResponse.java
 * @Description TODO
 * @createTime 2022/01/18 00:05:00
 */
public class HttpResponse {
    private Map<String, Object> data = new HashMap<>();
    private int statusCode = ResponseCode.OK;
    private String errorMessae="";

    public HttpResponse(Map<String, Object> data) {
        this.data = data;
    }

    public HttpResponse(int statusCode, String errorMessae) {
        this.statusCode = statusCode;
        this.errorMessae = errorMessae;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessae() {
        return errorMessae;
    }

    public void setErrorMessae(String errorMessae) {
        this.errorMessae = errorMessae;
    }

    public String toJsonString() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status_code", statusCode);
        resp.put("error_message", errorMessae);
        resp.put("data", data);
        return JSON.toJSONString(resp);
    }



    class ResponseCode {
        public static final int OK = 200;
        public static final int INTERNAL_ERROR = 500;
        public static final int BAD_REQUEST = 400;
    }

}
