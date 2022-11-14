package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.gaojy.rice.common.constants.ScheduleType;
import com.gaojy.rice.common.constants.TaskInstanceStatus;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;
import java.io.IOException;
import java.util.Date;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author gaojy
 * @ClassName HttpTaskExecuter.java
 * @Description http执行器： 支持 POST / GET
 * GET： task_desc  保存url
 * POST: task_desc  保存url,  parameters 支持json参数
 * @createTime 2022/11/13 22:11:00
 */
public class HttpTaskExecuter implements RiceExecuter {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    private final TaskScheduleClient client;
    private final String method;

    public HttpTaskExecuter(TaskScheduleClient client, String method) {
        this.client = client;
        this.method = method;
    }

    @Override
    public void execute(Long taskInstanceId) {
        final TaskInstanceInfo mainTaskInstance = client
            .getRepository()
            .getTaskInstanceInfoDao()
            .getInstance(taskInstanceId);

        if (this.client.getScheduleType().equals(ScheduleType.FIX_DELAY)) {
            handleHttpTask(mainTaskInstance);
        } else { // 异步执行
            this.client.getThreadPool().execute(() -> {
                HttpTaskExecuter.this.handleHttpTask(mainTaskInstance);
            });
        }

    }

    public void handleHttpTask(TaskInstanceInfo mainTaskInstance) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        int timeOutMillSec = Integer.parseInt(client.getTimeOutSec() + "") * 1000;
        RequestConfig requestConfig = RequestConfig.custom()
            // 设置连接超时时间(单位毫秒)
            .setConnectTimeout(timeOutMillSec)
            // 设置请求超时时间(单位毫秒)
            .setConnectionRequestTimeout(timeOutMillSec)
            // socket读写超时时间(单位毫秒)
            .setSocketTimeout(timeOutMillSec)
            // 设置是否允许重定向(默认为true)
            .setRedirectsEnabled(true).build();
        int retryCount = 0;
        while (retryCount <= client.getTaskRetryCount()) {
            try {
                // GET
                if (this.method.equalsIgnoreCase(METHOD_GET)) {

                    HttpGet httpGet = new HttpGet(this.client.getTaskDesc());
                    // 将上面的配置信息 运用到这个Get请求里
                    httpGet.setConfig(requestConfig);
                    response = httpClient.execute(httpGet);

                } else if (this.method.equalsIgnoreCase(METHOD_POST)) { // POST
                    HttpPost httpPost = new HttpPost("http://localhost:12345/index.html/");
                    httpPost.setConfig(requestConfig);
                    StringEntity entity = new StringEntity(client.getParameters(), "UTF-8");
                    httpPost.setEntity(entity);

                    httpPost.setHeader("Content-Type", "application/json;charset=utf8");

                    response = httpClient.execute(httpPost);
                }

                // 从响应模型中获取响应实体
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    mainTaskInstance.setResult(EntityUtils.toString(responseEntity));
                }
                mainTaskInstance.setStatus(TaskInstanceStatus.FINISHED.getCode());
                break;
            } catch (ParseException | IOException e) {
                mainTaskInstance.setStatus(TaskInstanceStatus.EXCEPTION.getCode());
                retryCount++;
            } finally {
                try {
                    // 释放资源
                    if (httpClient != null) {
                        httpClient.close();
                    }
                    if (response != null) {
                        response.close();
                    }
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
