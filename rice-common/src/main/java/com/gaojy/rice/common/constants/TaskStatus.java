package com.gaojy.rice.common.constants;

/**
 * @author gaojy
 * @ClassName TaskSataus.java
 * @Description 
 * @createTime 2022/02/11 14:32:00
 */
public enum TaskStatus {

    OFFLINE(0), ONLINE(1), PAUSE(2);
    private int code;

    TaskStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
