package com.gaojy.rice.common.constants;

/**
 * @author gaojy
 * @ClassName TaskType.java
 * @Description 任务类型
 * @createTime 2022/01/04 19:16:00
 */
public enum TaskType {

    /**
     * SHELL 任务
     */
    SHELL(0),

    /**
     * Python 任务
     */
    PYTHON(1),

    /**
     * Http Get任务
     */
    HTTP_GET(2),

    /**
     * Http Post任务
     */
    HTTP_POST(7),

    /**
     * JAVA内置任务
     */
    BASIC_JAVA_INTERNAL(3),

    /**
     * MAP 任务
     */
    RICE_MAP(4),

    /**
     * MAP REDUCE 任务
     */
    RICE_MAPREDUCE(5),

    /**
     * 工作流任务
     */
    RICE_WORKFLOW(6);

    private int code;

    TaskType(int code) {
        this.code = code;
    }

    public static TaskType getType(int type) {
        return TaskType.values()[type];
    }

    public int getCode() {
        return code;
    }
}
