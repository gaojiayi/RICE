<h3>一个简单的JAVA内置任务调度</h3>

假设有一个任务，该任务分布在多个实例上，现在需要每3秒钟全局执行一次。

<h4>任务注册</h4>

在控制台上分别注册一个业务线和在该业务线下创建一个task。


<h4>编写一个基本的java任务</h4>

```
package com.gaojy.rice.demo.processor.basic;

import com.gaojy.rice.processor.api.ProcessResult;
import com.gaojy.rice.processor.api.RiceBasicProcessor;
import com.gaojy.rice.processor.api.TaskContext;
import com.gaojy.rice.processor.api.annotation.Executer;
import com.gaojy.rice.processor.api.annotation.LogEnable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName BasicDemoTask.java
 * @Description
 * @createTime 2022/11/05 11:52:00
 */
@Executer(taskCode = "demoTaskCode",taskName = "demoTaskName")
@LogEnable
public class BasicDemoTask implements RiceBasicProcessor {
    @Override
    public ProcessResult process(TaskContext context) {
        //  处理具体的任务逻辑
        ProcessResult result = new ProcessResult();
        Map<Object, Object> map = new HashMap<>();
        map.put("result", "success");
        result.setData(map);
        return result;
    }
}

```

<h4>启动任务处理器</h4>

```
package com.gaojy.rice.demo.processor;

import com.gaojy.rice.processor.api.RiceProcessorManager;

/**
 * @author gaojy
 * @ClassName ProcessorServiceInstance.java
 * @Description
 * @createTime 2022/11/04 22:58:00
 */
public class ProcessorServiceInstance {
    public static void main(String[] args) {
        // 初始化rice处理器管理
        RiceProcessorManager manager = RiceProcessorManager.getManager();
        // 任务暴露
        manager.export();
    }
}

```

<h4>查看任务执行状态</h4>

可以看到有一条处理器上线的记录

<img src="../../../assets/processor-online-db.png" alt="处理器上线记录">

在任务上线通知中有了一条change的记录,这样调度器可以通过长轮询来获取到任务处理器上线的通知

<img src="../../../assets/processor-online-change-db.png" alt="任务上线通知">

最后可以看到数据库每3s执行一次,可以看到什么时候执行,什么时候结束,执行结果等

<img src="../../../assets/task-instance-record-db.png" alt="任务执行记录">
