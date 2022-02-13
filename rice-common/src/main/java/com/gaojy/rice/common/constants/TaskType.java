package com.gaojy.rice.common.constants;

/**
 * @author gaojy
 * @ClassName TaskType.java
 * @Description 任务类型
 * @createTime 2022/01/04 19:16:00
 */
public enum TaskType {
    /**
     * 基本的RICE任务
     */
    RICE_BASIC(0),
    /**
     * MAP 任务
     */
    RICE_MAP(1),
    /**
     * MAP REDUCE 任务
     */
    RICE_MAPREDUCE(2),
    /**
     * 工作流任务
     */
    RICE_WORKFLOW(3);

    private int code;

    TaskType(int code) {
        this.code = code;
    }

    public  static TaskType getType(int type){
        return TaskType.values()[type];
    }
}
