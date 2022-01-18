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
    private int respCode;
    private String errorMessae="";

    public HttpResponse(Map<String, Object> data) {
        this.data = data;
    }

    public HttpResponse(int respCode, String errorMessae) {
        this.respCode = respCode;
        this.errorMessae = errorMessae;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }


    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getErrorMessae() {
        return errorMessae;
    }

    public void setErrorMessae(String errorMessae) {
        this.errorMessae = errorMessae;
    }

    public String toJsonString() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("resp_code", respCode);
        resp.put("error_message", errorMessae);
        resp.put("data", data);
        return JSON.toJSONString(resp);
    }





}
