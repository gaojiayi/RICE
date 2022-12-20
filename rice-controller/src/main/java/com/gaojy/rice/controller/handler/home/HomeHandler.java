package com.gaojy.rice.controller.handler.home;

import com.gaojy.rice.common.constants.TaskInstanceStatus;
import com.gaojy.rice.common.entity.RiceAppGroupInfo;
import com.gaojy.rice.common.entity.RiceAppInfo;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.common.exception.ControllerException;
import com.gaojy.rice.controller.RiceControllerBootStrap;
import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.controller.maintain.SchedulerManager;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.RequestMapping;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gaojy
 * @ClassName HomeHandler.java
 * @Description 首页
 * @createTime 2022/12/04 23:34:00
 */
public class HomeHandler extends AbstractHttpHandler {

    public HomeHandler(String rootPath) {
        super(rootPath);
    }

    @RequestMapping(value = "/chart", method = "GET")
    public HttpResponse chart(HttpRequest request) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();

        Map<String, Object> taskRateMap = new HashMap<>();
        taskRateMap.put("timeout",
            repository.getTaskInstanceInfoDao().getNumByStatus(TaskInstanceStatus.TIMEOUT.getCode())
        );
        taskRateMap.put("success",
            repository.getTaskInstanceInfoDao().getNumByStatus(TaskInstanceStatus.FINISHED.getCode()));
        taskRateMap.put("execption",
            repository.getTaskInstanceInfoDao().getNumByStatus(TaskInstanceStatus.EXCEPTION.getCode()));
        taskRateMap.put("failed",
            repository.getTaskInstanceInfoDao().getNumByStatus(TaskInstanceStatus.RUNNING.getCode()));
        responseMap.put("taskSuccessRate", taskRateMap);

        List<RiceAppGroupInfo> appGroups = repository.getRiceAppInfoDao().getCountByName(10);
        responseMap.put("appProcessorNumRank", appGroups);

        List<Map> dateNumList = new ArrayList<>();
        for (int i = 7 - 1; i >= 0; i--) {
            String pastDateStr = getPastDateStr(i);
            Integer numOfProcessorInDate = repository.getProcessorServerInfoDao().getNumOfProcessorInDate(
                parseDateByStr(pastDateStr + " 00:00:00"),
                parseDateByStr(pastDateStr + " 23:59:59"));
            Map<String, Integer> dateNumMap = new HashMap<>();
            dateNumMap.put(pastDateStr, numOfProcessorInDate);
            dateNumList.add(dateNumMap);
        }
        responseMap.put("scheduleNumForWeek", dateNumList);

        return new HttpResponse(responseMap);
    }

    @RequestMapping(value = "/controller/info", method = "GET")
    public HttpResponse controllerInfo(HttpRequest request) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        final List<HomeHandler.ControllerInfo> controllerInfoList = new ArrayList<>();

        String leader = "NO_LEADER";
        try {
            leader = RiceControllerBootStrap.getController().getReplicatorManager().getLeader();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        final String leaderInfo = String.valueOf(leader);

        Long bootTime = RiceControllerBootStrap.getController().getBootTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String time = formatDate.format(bootTime);
        responseMap.put("start_time", time);

        final String localHost = RiceControllerBootStrap.getController().getControllerConfig().getLocalHost();
        final int localPort = RiceControllerBootStrap.getController().getControllerConfig().getControllerPort();

        String allControllerAddressStr = RiceControllerBootStrap.getController().getControllerConfig().getAllControllerAddressStr();
        Arrays.asList(allControllerAddressStr.split(",")).stream().forEach(endpoint -> {
            HomeHandler.ControllerInfo controllerInfo = new HomeHandler.ControllerInfo();
            controllerInfo.setAddress(endpoint);
            controllerInfo.setCurrent((localHost + ":" + localPort).equals(endpoint));
            controllerInfo.setMaster(leaderInfo.equals(endpoint));
            controllerInfo.setStatus(0);
            controllerInfoList.add(controllerInfo);
        });
        responseMap.put("collectors", controllerInfoList);
        return new HttpResponse(responseMap);
    }

    @RequestMapping(value = "/metrics", method = "GET")
    public HttpResponse metrics(HttpRequest request) throws Exception {
        List<String> allValidTaskCode = repository.getRiceTaskInfoDao().getAllValidTaskCode();
        Integer countValidInstance = repository.getTaskInstanceInfoDao().getCountValidInstance();
        int schedulerServerNum = SchedulerManager.getManager().getActiveScheduler().size();
        return new HttpResponse()
            .addResponse("task_num", allValidTaskCode.size())
            .addResponse("total_schedule_times", countValidInstance)
            .addResponse("scheduler_num", schedulerServerNum);
    }

    @RequestMapping(value = "/latest/task", method = "GET")
    public HttpResponse scrollBar(HttpRequest request) throws Exception {
        final List<Map> instanceList = new ArrayList<>();
        Object limit = request.getParamMap().get("limit");
        if (limit == null) {
            limit = 10;
        }
        List<TaskInstanceInfo> latestInstances = repository.getTaskInstanceInfoDao()
            .getLatestInstance(Integer.parseInt(limit.toString()));

        // 查询task
        List<String> taskCodes = latestInstances.stream().map(TaskInstanceInfo::getTaskCode).distinct().collect(Collectors.toList());
        List<RiceTaskInfo> taskInfos = repository.getRiceTaskInfoDao().getInfoByCodes(taskCodes);
        final Map<String, RiceTaskInfo> taskInfoMap = new HashMap<>();
        taskInfos.forEach(info -> {
            taskInfoMap.put(info.getTaskCode(), info);
        });
        // app
        List<Long> appIds = taskInfos.stream().map(RiceTaskInfo::getAppId).distinct().collect(Collectors.toList());
        List<RiceAppInfo> riceAppInfos = repository.getRiceAppInfoDao().queryAppsByIds(appIds);
        final Map<Long, String> appNameMap = new HashMap<>();

        riceAppInfos.forEach(app -> {
            appNameMap.put(app.getId(), app.getAppName());
        });
        SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss MM-dd-MM-yyyy ");

        latestInstances.forEach(instanceInfo -> {
            HashMap<String, Object> item = new HashMap<>();
            item.put("trigger_time", formatDate.format(instanceInfo.getActualTriggerTime()));
            item.put("task_name", taskInfoMap.get(instanceInfo.getTaskCode()).getTaskName());
            item.put("app_name", appNameMap.get(taskInfoMap.get(instanceInfo.getTaskCode()).getAppId()));
            item.put("processor_address", instanceInfo.getTaskTrackerAddress() == null ?
                "" : instanceInfo.getTaskTrackerAddress());
            item.put("scheduler_address", taskInfoMap.get(instanceInfo.getTaskCode()).getSchedulerServer());
            item.put("status", instanceInfo.getStatus());
            instanceList.add(item);
        });
        return new HttpResponse("instances", instanceList);
    }

    class ControllerInfo {

        String address;
        Boolean isMaster;
        Boolean isCurrent;
        Integer status;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Boolean getMaster() {
            return isMaster;
        }

        public void setMaster(Boolean master) {
            isMaster = master;
        }

        public Boolean getCurrent() {
            return isCurrent;
        }

        public void setCurrent(Boolean current) {
            isCurrent = current;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }

    private String getPastDateStr(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    private Date parseDateByStr(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new ControllerException(e);
        }
    }

}
