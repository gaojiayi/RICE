package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.constants.TaskOptType;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.common.protocol.body.scheduler.SchedulerHeartBeatBody;
import com.gaojy.rice.common.protocol.header.scheduler.SchedulerPullTaskChangeRequestHeader;
import com.gaojy.rice.common.protocol.header.scheduler.SchedulerRegisterRequestHeader;
import com.gaojy.rice.controller.replicator.ControllerClosure;
import com.gaojy.rice.controller.replicator.ControllerDataService;
import com.gaojy.rice.controller.replicator.SchedulerData;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.TransportServer;
import com.gaojy.rice.repository.api.Repository;
import com.gaojy.rice.repository.api.dao.ProcessorServerInfoDao;
import com.gaojy.rice.repository.api.dao.RiceTaskChangeRecordDao;
import com.gaojy.rice.repository.api.dao.RiceTaskInfoDao;
import com.gaojy.rice.repository.api.dao.TaskInstanceInfoDao;
import com.gaojy.rice.repository.mysql.impl.RiceTaskChangeRecordDaoImpl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * @author gaojy
 * @ClassName TestSchedulerManagerProcessor.java
 * @Description
 * @createTime 2022/08/13 11:24:00
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSchedulerManagerProcessor extends AbstractTestProcessor {
    TransportServer transportServer;

    @Before
    public void init() throws InterruptedException {
        doReturn(true).when(riceController).isMaster();

        ControllerDataService controllerDataService = new ControllerDataService() {
            @Override public List<Map<String, String>> querySchedulersData(boolean readOnlySafe,
                ControllerClosure closure) {
                return null;
            }

            @Override public void updateSchedulerData(String schedulerAddress, SchedulerData data,
                ControllerClosure closure) {

            }
//
        };
        Repository repository = new Repository() {

            @Override public void connect() {

            }

            @Override public void close() {

            }

            @Override public RiceTaskChangeRecordDao getRiceTaskChangeRecordDao() {
                RiceTaskChangeRecordDao dao = spy(new RiceTaskChangeRecordDaoImpl());
                TaskChangeRecord record = new TaskChangeRecord();
                record.setTaskCode("TEST_TASK_CODE");
                record.setOptType(TaskOptType.TASK_PAUSE.getCode());
                record.setCreateTime(new Date());
                ArrayList<TaskChangeRecord> objects = new ArrayList<>();
                objects.add(record);
                doReturn(objects).when(dao).getChanges(anyString(), anyLong());
                return dao;
            }

            @Override public ProcessorServerInfoDao getProcessorServerInfoDao() {
                return null;
            }

            @Override public RiceTaskInfoDao getRiceTaskInfoDao() {
                return null;
            }

            @Override public TaskInstanceInfoDao getTaskInstanceInfoDao() {
                return null;
            }
        };
        doReturn(controllerDataService).when(riceController).getControllerDataService();
        doReturn(repository).when(riceController).getRepository();

        SchedulerManagerProcessor processor = new SchedulerManagerProcessor(riceController);

        transportServer = buildTransportServer();
        transportServer.registerProcessor(RequestCode.SCHEDULER_REGISTER, processor, Executors.newCachedThreadPool());
        transportServer.registerProcessor(RequestCode.SCHEDULER_HEART_BEAT, processor, Executors.newCachedThreadPool());
        transportServer.registerProcessor(RequestCode.SCHEDULER_PULL_TASK, processor, Executors.newCachedThreadPool());

        transportServer.start();
    }

    @Test
    public void testSchedulerRegister() throws InterruptedException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {

        // send request
        SchedulerRegisterRequestHeader header = new SchedulerRegisterRequestHeader();
        header.setFirstRegister(true);
        header.setBootTime(System.currentTimeMillis());
        RiceRemoteContext command = RiceRemoteContext.createRequestCommand(RequestCode.SCHEDULER_REGISTER, header);
        RiceRemoteContext response = transportClient.invokeSync("localhost:8888", command, 1000 * 100);
        int code = response.getCode();
        Assert.assertEquals(ResponseCode.SUCCESS, code);
    }

    @Test
    public void TestHeartBeat() throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, InterruptedException, RemotingTooMuchRequestException {

        SchedulerHeartBeatBody schedulerHeartBeatBody = new SchedulerHeartBeatBody();

        SchedulerHeartBeatBody.ProcessorDetail processorDetail = new SchedulerHeartBeatBody.ProcessorDetail();
        processorDetail.setPort(1111);
        processorDetail.setAddress("110.22.13.10");
        processorDetail.setLatestActiveTime(System.currentTimeMillis());
        schedulerHeartBeatBody.addProcessorDetail(processorDetail);

        schedulerHeartBeatBody.setCPURate(5D);
        schedulerHeartBeatBody.setMenRate(30D);

        ArrayList<String> taskCodes = new ArrayList<>();
        taskCodes.add("TEST_TASK_CODE");
        schedulerHeartBeatBody.setTaskCodes(taskCodes);

        RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.SCHEDULER_HEART_BEAT, null);
        requestCommand.setBody(schedulerHeartBeatBody.encode());
        transportClient.invokeOneWay("localhost:8888", requestCommand, 1000 * 100);

    }

    @Test
    public void testPullTask() throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, InterruptedException {
        SchedulerPullTaskChangeRequestHeader header = new SchedulerPullTaskChangeRequestHeader();
        header.setTaskCode("TEST_TASK_CODE");
        header.setLastTaskChangeTimestamp(System.currentTimeMillis());
        RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.SCHEDULER_PULL_TASK, header);
        RiceRemoteContext response = transportClient.invokeSync("localhost:8888", requestCommand, 1000 * 100);
        int code = response.getCode();
        Assert.assertEquals(ResponseCode.SUCCESS, code);
    }

    @After
    public void clear() {
        if (transportServer != null) {
            transportServer.shutdown();
        }
    }

}
