package com.gaojy.rice.processor.api;

/**
 * @author gaojy
 * @ClassName RiceMapProcessor.java
 * @Description 
 * @createTime 2022/01/04 19:44:00
 */
public interface RiceMapProcessor extends RiceBasicProcessor {

    public ProcessResult map(TaskContext context);
}
