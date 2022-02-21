package com.gaojy.rice.processor.api.invoker;

import com.gaojy.rice.processor.api.ProcessResult;
import com.gaojy.rice.processor.api.RiceBasicProcessor;
import com.gaojy.rice.processor.api.RiceMapProcessor;
import com.gaojy.rice.processor.api.TaskContext;
import com.gaojy.rice.processor.api.annotation.Executer;

/**
 * @author gaojy
 * @ClassName DemoProcessor.java
 * @Description TODO
 * @createTime 2022/01/26 12:08:00
 */
@Executer(taskCode = "demoTaskCode",taskName = "demoTaskName")
public class DemoProcessor implements RiceMapProcessor {
    @Override
    public ProcessResult process(TaskContext context) {
        return null;
    }

    @Override public ProcessResult map(TaskContext context) {
        return null;
    }
}
