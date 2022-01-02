package com.gaojy.rice.processor;

/**
 * @author gaojy
 * @ClassName RiceTaskProcessor.java
 * @Description TODO
 * @createTime 2022/01/02 13:55:00
 */
public interface RiceTaskProcessor {

    public ProcessResult process(TaskContext context);
}
