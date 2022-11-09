package com.gaojy.rice.demo.processor.basic;

import com.gaojy.rice.processor.api.ProcessResult;
import com.gaojy.rice.processor.api.RiceBasicProcessor;
import com.gaojy.rice.processor.api.TaskContext;
import com.gaojy.rice.processor.api.annotation.Executer;
import com.gaojy.rice.processor.api.annotation.LogEnable;
import com.gaojy.rice.processor.api.log.RiceClientLogger;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName BasicDemoTask.java
 * @Description
 * @createTime 2022/11/05 11:52:00
 */
@Executer(taskCode = "demoTaskCode",taskName = "demoTaskName")
@LogEnable
public class BasicDemoTask implements RiceBasicProcessor {
    private static final  Logger logger = LoggerFactory.getLogger("ProcessorServiceInstance");

    @Override
    public ProcessResult process(TaskContext context) {
        ProcessResult result = new ProcessResult();
        RiceClientLogger.getLog().debug("这个是RiceClientLogger打印出的日志");

        logger.info("这个是使用logback.xml初始化的log实例打印的日志");
        Map<Object, Object> map = new HashMap<>();
        map.put("result", "success");
        result.setData(map);
        return result;
    }
}
