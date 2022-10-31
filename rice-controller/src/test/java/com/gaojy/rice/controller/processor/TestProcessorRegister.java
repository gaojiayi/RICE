package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.ExecuteType;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.constants.TaskType;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskRequestBody;
import com.gaojy.rice.common.protocol.body.processor.TaskDetailData;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskRequestHeader;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.protocol.SerializeType;
import com.gaojy.rice.remote.transport.TransportServer;
import com.gaojy.rice.repository.api.Repository;
import com.gaojy.rice.repository.api.dao.ProcessorServerInfoDao;
import com.gaojy.rice.repository.api.dao.RiceTaskChangeRecordDao;
import com.gaojy.rice.repository.api.dao.RiceTaskInfoDao;
import com.gaojy.rice.repository.api.dao.TaskInstanceInfoDao;
import com.gaojy.rice.repository.mysql.impl.ProcessorServerInfoDaoImpl;
import com.gaojy.rice.repository.mysql.impl.RiceTaskChangeRecordDaoImpl;
import com.gaojy.rice.repository.mysql.impl.RiceTaskInfoDaoImpl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * @author gaojy
 * @ClassName TestTaskAccessorProcessor.java
 * @Description
 * @createTime 2022/01/23 20:23:00
 */
@RunWith(MockitoJUnitRunner.class)
public class TestProcessorRegister extends AbstractTestProcessor {
    @Test
    public void taskAccessorProcessor() throws InterruptedException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        TransportServer transportServer = this.buildTransportServer();
        Repository p = new Repository() {
            @Override public void connect() {

            }

            @Override public void close() {

            }

            @Override public RiceTaskChangeRecordDao getRiceTaskChangeRecordDao() {
                RiceTaskChangeRecordDaoImpl spy = spy(new RiceTaskChangeRecordDaoImpl());
                doNothing().when(spy).insert(any(List.class));
                return spy;
            }

            @Override public ProcessorServerInfoDao getProcessorServerInfoDao() {
                ProcessorServerInfoDaoImpl spy = spy(new ProcessorServerInfoDaoImpl());
                List<ProcessorServerInfo> objects = new ArrayList<>();
                doReturn(objects).when(spy).getInfosByServer(anyLong(), anyString(), anyInt());
                doReturn(1).when(spy).batchCreateOrUpdateInfo(any(List.class));
                return spy;
            }

            @Override public RiceTaskInfoDao getRiceTaskInfoDao() {
                RiceTaskInfoDaoImpl spy = spy(new RiceTaskInfoDaoImpl());
                ArrayList<RiceTaskInfo> objects = new ArrayList<>();
                RiceTaskInfo riceTaskInfo = new RiceTaskInfo();
                riceTaskInfo.setTaskCode("TestTaskCode");
                riceTaskInfo.setTaskName("TestTaskName");
                riceTaskInfo.setAppId(1L);
                riceTaskInfo.setTaskType(TaskType.BASIC_JAVA_INTERNAL.getCode());
                riceTaskInfo.setTaskDesc("test");
                riceTaskInfo.setExecuteType(ExecuteType.STANDALONE.name());
                riceTaskInfo.setCreateTime(new Date());
                riceTaskInfo.setStatus(1);
                objects.add(riceTaskInfo);
                doReturn(objects).when(spy).getInfoByCodes(any(List.class));
                return spy;
            }

            @Override public TaskInstanceInfoDao getTaskInstanceInfoDao() {
                return null;
            }
        };

        //使用doReturn-when可以避免when-thenReturn调用真实对象api
        doReturn(true).when(riceController).isMaster();
        doReturn(p).when(riceController).getRepository();

        transportServer.registerProcessor(RequestCode.REGISTER_PROCESSOR,
            new TaskAccessProcessor(riceController), Executors.newCachedThreadPool());
        transportServer.start();

        ExportTaskRequestHeader header = new ExportTaskRequestHeader();
        header.setNetAddress("127.0.0.1");
        header.setListenPort(1111);
        header.setAppId("1");
        RiceRemoteContext request = RiceRemoteContext.createRequestCommand(RequestCode.REGISTER_PROCESSOR, header);

        ExportTaskRequestBody body = new ExportTaskRequestBody();
        List<TaskDetailData> list = new ArrayList<>();

        TaskDetailData data = new TaskDetailData();
        data.setTaskCode("TestTaskCode");
        data.setTaskName("TestTaskName");
        //data.setTaskType(TaskType.STANDALONE);
        data.setClassName("com.gaojy.rice.common.protocol.body.processor.ExportTaskRequestBody");
        list.add(data);
        body.setTasks(list);

        request.setBody(body.encode());
        request.setSerializeTypeCurrentRPC(SerializeType.RICE);

        RiceRemoteContext response = transportClient.invokeSync("localhost:8888", request, 1000 * 100);
        int code = response.getCode();
        Assert.assertEquals(ResponseCode.SUCCESS, code);

    }

}
