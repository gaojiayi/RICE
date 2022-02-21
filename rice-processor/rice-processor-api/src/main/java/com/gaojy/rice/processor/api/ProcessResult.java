package com.gaojy.rice.processor.api;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName ProcessResult.java
 * @Description TODO
 * @createTime 2022/01/02 13:57:00
 */
public class ProcessResult {

    private Map data = new HashMap<>();

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
