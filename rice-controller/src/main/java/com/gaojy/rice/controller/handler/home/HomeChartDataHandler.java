package com.gaojy.rice.controller.handler.home;

import com.gaojy.rice.common.constants.TaskInstanceStatus;
import com.gaojy.rice.common.entity.RiceAppGroupInfo;
import com.gaojy.rice.common.exception.ControllerException;
import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName HomeChartDataHandler.java
 * @Description
 * @createTime 2022/11/19 22:52:00
 */
public class HomeChartDataHandler extends AbstractHttpHandler {
    /**
     * task_success_rate: {
     * timeout: 3,
     * running: 204,
     * success: 1079,
     * exception: 79
     * },
     * app_processor_num_rank: [
     * {
     * app_name: "订单系统",
     * processor_num: 10
     * },
     * {
     * app_name: "商品系统",
     * processor_num: 15
     * },
     * {
     * app_name: "检索系统",
     * processor_num: 8
     * },
     * {
     * app_name: "营销系统",
     * processor_num: 5
     * }
     * ],
     * schedule_num_for_week: [
     * {
     * date: "2022-07-13",
     * num: 421
     * },
     * {
     * date: "2022-07-14",
     * num: 521
     * },
     * {
     * date: "2022-07-15",
     * num: 201
     * },
     * {
     * date: "2022-07-16",
     * num: 840
     * },
     * {
     * date: "2022-07-17",
     * num: 1200
     * },
     * {
     * date: "2022-07-18",
     * num: 612
     * },
     * {
     * date: "2022-07-19",
     * num: 752
     * }
     * ]
     */

    public HomeChartDataHandler(String path) {
        super(path);
    }

    @Override
    public HttpResponse handler(HttpRequest request) throws Exception {
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
