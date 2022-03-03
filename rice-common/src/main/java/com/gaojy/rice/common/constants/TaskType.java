package com.gaojy.rice.common.constants;

/**
 * @author gaojy
 * @ClassName TaskType.java
 * @Description 任务类型
 * @createTime 2022/01/04 19:16:00
 */
public enum TaskType {

    /**
     * 广播执行
     */
    BROADCAST(0),

    /**
     * 单机执行
     */
    STANDALONE(1),

    /**
     * MAP 任务
     */
    RICE_MAP(2),
    /**
     * MAP REDUCE 任务
     */
    RICE_MAPREDUCE(3),
    /**
     * 工作流任务
     */
    RICE_WORKFLOW(4);



    private int code;

    TaskType(int code) {
        this.code = code;
    }

    public  static TaskType getType(int type){
        return TaskType.values()[type];
    }
}
