package com.gaojy.rice.common.constants;

import java.util.Arrays;

/**
 * @author gaojy
 * @ClassName TaskOptType.java
 * @Description 任务操作类型  主要用于长连接处理
 * @createTime 2022/02/13 17:22:00
 */
public enum TaskOptType {

    TASK_PROCESSOR_ONLINE(1, "任务处理器上线"),

    TASK_PROCESSOR_ISOLATION(2, "任务处理器隔离"),

    TASK_DELETE(3, "任务删除"),

    TASK_PAUSE(4, "任务暂停"),

    TASK_START(5, "任务启动"),

    TASK_UPDATE(6, "任务更新"),

    TASK_RUN(7, "任务立即运行"),

//    SCHEDULER_ONLINE(8, "调度器上线"),
//
//    SCHEDULER_OFFLINE(9, "调度器下线"),

    UNKNOWN(10,"未知");

    private int code;
    private String name;

    TaskOptType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static TaskOptType getTaskOptType(int optType) {
        return Arrays.stream(TaskOptType.values()).filter(type -> {
            return type.getCode() == optType;
        }).findFirst().orElse(UNKNOWN);
    }

    public static void main(String[] args) {
        Arrays.stream(TaskOptType.values()).forEach(t->{
            System.out.print(t.code+":"+t.name+" ");
        });
    }

}

