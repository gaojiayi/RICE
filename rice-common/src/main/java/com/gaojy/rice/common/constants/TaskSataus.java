package com.gaojy.rice.common.constants;

/**
 * @author gaojy
 * @ClassName TaskSataus.java
 * @Description TODO
 * @createTime 2022/02/11 14:32:00
 */
public enum TaskSataus {

    OFFLINE(0), ONLINE(1), PAUSE(2);
    private int code;

    TaskSataus(int code) {
        this.code = code;
    }
}
