package com.gaojy.rice.controller.handler.home;

import com.gaojy.rice.common.constants.TaskInstanceStatus;
import com.gaojy.rice.common.entity.RiceAppGroupInfo;
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
        repository.getTaskInstanceInfoDao().getNumByStatus(TaskInstanceStatus.FINISHED.getCode());
        repository.getTaskInstanceInfoDao().getNumByStatus(TaskInstanceStatus.EXCEPTION.getCode());
        repository.getTaskInstanceInfoDao().getNumByStatus(TaskInstanceStatus.RUNNING.getCode());
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
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String leader = RiceControllerBootStrap.getController().getReplicatorManager().getLeader();

        Long bootTime = RiceControllerBootStrap.getController().getBootTime();
        String time = formatDate.format(bootTime);
        responseMap.put("start_time", time);

        final String localHost = RiceControllerBootStrap.getController().getControllerConfig().getLocalHost();
        final int localPort = RiceControllerBootStrap.getController().getControllerConfig().getControllerPort();

        String allControllerAddressStr = RiceControllerBootStrap.getController().getControllerConfig().getAllControllerAddressStr();
        Arrays.asList(allControllerAddressStr.split(",")).stream().forEach(endpoint -> {
            HomeHandler.ControllerInfo controllerInfo = new HomeHandler.ControllerInfo();
            controllerInfo.setAddress(endpoint);
            controllerInfo.setCurrent((localHost + ":" + localPort).equals(endpoint));
            controllerInfo.setMaster(leader.equals(endpoint));
            controllerInfo.setStatus(0);
            controllerInfoList.add(controllerInfo);
        });
        responseMap.put("collectors", controllerInfoList);
        return new HttpResponse(responseMap);
    }

    @RequestMapping(value = "/metrics", method = "GET")
    public HttpResponse metrics(HttpRequest request) throws Exception {
        Map<String, Integer> responseMap = new HashMap<>();
        List<String> allValidTaskCode = repository.getRiceTaskInfoDao().getAllValidTaskCode();
        Integer countValidInstance = repository.getTaskInstanceInfoDao().getCountValidInstance();
        int schedulerServerNum = SchedulerManager.getManager().getActiveScheduler().size();
        return new HttpResponse()
            .addResponse("task_num", allValidTaskCode.size())
            .addResponse("total_schedule_times", countValidInstance)
            .addResponse("scheduler_num", schedulerServerNum);
    }

    @RequestMapping(value = "/scrollBar", method = "GET")
    public HttpResponse scrollBar(HttpRequest request) throws Exception {
        HashMap<String, List<TaskInstanceInfo>> responseMap = new HashMap<>();
        Object limit = request.getParamMap().get("limit");
        if (limit == null) {
            limit = 10;
        }
        List<TaskInstanceInfo> latestInstances = repository.getTaskInstanceInfoDao()
            .getLatestInstance(Integer.parseInt(limit.toString()));
        return new HttpResponse("instances", latestInstances);
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
