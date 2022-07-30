package com.gaojy.rice.processor.api.invoker;

import com.gaojy.rice.processor.api.ProcessResult;
import com.gaojy.rice.processor.api.RiceBasicProcessor;
import com.gaojy.rice.processor.api.RiceMapProcessor;
import com.gaojy.rice.processor.api.TaskContext;
import com.gaojy.rice.processor.api.annotation.Executer;
import com.gaojy.rice.processor.api.annotation.LogEnable;
import java.util.HashMap;

/**
 * @author gaojy
 * @ClassName DemoProcessor.java
 * @Description
 * @createTime 2022/01/26 12:08:00
 */
@Executer(taskCode = "demoTaskCode",taskName = "demoTaskName")
@LogEnable
public class DemoProcessor implements RiceMapProcessor {
    @Override
    public ProcessResult process(TaskContext context) {
        ProcessResult processResult = new ProcessResult();
        HashMap<Object, Object> data= new HashMap<>();
        data.put("result","success");
        processResult.setData(data);
        return processResult;
    }

    @Override
    public ProcessResult map(TaskContext context) {
        ProcessResult processResult = new ProcessResult();
        HashMap<Object, Object> data= new HashMap<>();
        data.put("parameter1","a");
        data.put("parameter1","b");
        data.put("parameter1","c");
        data.put("parameter1","d");
        processResult.setData(data);
        return processResult;
    }
}
