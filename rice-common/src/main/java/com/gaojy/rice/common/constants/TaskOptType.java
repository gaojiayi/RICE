package com.gaojy.rice.common.constants;

import java.util.Arrays;

/**
 * @author gaojy
 * @ClassName TaskOptType.java
 * @Description 任务操作类型  主要用于长连接处理
 * @createTime 2022/02/13 17:22:00
 */
public enum TaskOptType {

    TASK_PROCESSOR_ONLINE(1),

    TASK_PROCESSOR_OFFLINE(2),

    TASK_DELETE(3),

    TASK_PAUSE(4),

    TASK_RUNNING(5),

    TASK_UPDATE(6),

    UNKNOWN(0);

    private int code;

    TaskOptType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TaskOptType getTaskOptType(int optType){
        return Arrays.stream(TaskOptType.values()).filter(type -> {
            return type.getCode() == optType;
        }).findFirst().orElse(UNKNOWN);
    }

}

