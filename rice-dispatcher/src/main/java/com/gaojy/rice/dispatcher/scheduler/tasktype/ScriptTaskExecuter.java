package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.gaojy.rice.common.constants.ScheduleType;
import com.gaojy.rice.common.constants.TaskInstanceStatus;
import com.gaojy.rice.common.constants.TaskType;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Date;


/**
 * @author gaojy
 * @ClassName ScriptTaskExcuter.java
 * @Description
 * @createTime 2022/11/14 21:59:00
 */
public class ScriptTaskExecuter implements RiceExecuter {
//   https://blog.csdn.net/wojiushiwo945you/article/details/80073227

    private TaskScheduleClient client;

    private String scriptType;

    public ScriptTaskExecuter(TaskScheduleClient client, String scriptType) {
        this.client = client;
        this.scriptType = scriptType;
    }

    @Override
    public void execute(Long taskInstanceId) {
        final TaskInstanceInfo mainTaskInstance = client
            .getRepository()
            .getTaskInstanceInfoDao()
            .getInstance(taskInstanceId);

        if (this.client.getScheduleType().equals(ScheduleType.FIX_DELAY)) {
            handleScriptTask(mainTaskInstance);
        } else { // 异步执行
            this.client.getThreadPool().execute(() -> {
                ScriptTaskExecuter.this.handleScriptTask(mainTaskInstance);
            });
        }
    }

    private void handleScriptTask(TaskInstanceInfo mainTaskInstance) {
        Process proc = null;
        BufferedReader in = null;
        int retryCount = 0;
        while (retryCount <= client.getTaskRetryCount()) {
            try {
                if (client.getTaskType() == TaskType.SHELL) {
                    proc = Runtime.getRuntime().exec("/bin/sh " + client.getParameters());

                } else if (client.getTaskType() == TaskType.PYTHON) {

                    proc = Runtime.getRuntime().exec("python " + client.getParameters());// 执行py文件
                }

                in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String result = in.readLine();
                String line = "";
                while ((line = in.readLine()) != null) {
                    result = result + "," + line;
                }
                mainTaskInstance.setResult(result);
                mainTaskInstance.setStatus(TaskInstanceStatus.FINISHED.getCode());
                proc.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                mainTaskInstance.setResult(e.getMessage());
                mainTaskInstance.setStatus(TaskInstanceStatus.EXCEPTION.getCode());
                retryCount++;
            } finally {
                if (proc != null) {
                    proc.destroy();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            mainTaskInstance.setRetryTimes(retryCount);
            mainTaskInstance.setFinishedTime(new Date());
            client.getRepository().getTaskInstanceInfoDao().updateTaskInstance(mainTaskInstance);
        }

    }

}
