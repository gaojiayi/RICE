package com.gaojy.rice.controller.config;

import com.gaojy.rice.common.constants.ElectionConstants;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.utils.MixAll;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName ControllerConfig.java
 * @Description
 * @createTime 2022/01/13 22:18:00
 */
public class ControllerConfig extends ElectionConstants {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);

    private int controllerPort = -1;

    private String configStorePath = System.getProperty("user.home") + File.separator + "rice" + File.separator + "rice-controller.properties";

    private String riceHome = System.getProperty(MixAll.RICE_CONTROLLER_HOME_PROPERTY, System.getenv(MixAll.RICE_HOME_ENV));

    /**
     * 控制器选举和数据复制端口   默认是业务端口-2
     */
    private int controllerElectionPort = -1;

    /**
     * 前端控制台的管理端口
     */
    private int managePort = 9090;

    private String dataPath = "/tmp/rice/controller/data";

    /**
     * 所有控制器的业务集群
     * allControllerAddressStr 和 localHost
     * 可以计算出allElectionAddressStr和controllerPort
     */
    private String allControllerAddressStr = "";
    /**
     * 所有控制器的选举集群
     */
    private String allElectionAddressStr = "";

    private String localHost = "127.0.0.1";

    private int taskAccessThreadPoolNums = 16;

    /**
     * 处理来自调度器的请求和长轮询处理的线程池
     */
    private int schedulerManagerThreadPoolNums = 16 + Runtime.getRuntime().availableProcessors() * 2;

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public String getAllControllerAddressStr() {
        return allControllerAddressStr;
    }

    public void setAllControllerAddressStr(String allControllerAddressStr) {
        this.allControllerAddressStr = allControllerAddressStr;
        final List<String> endpoints = new ArrayList<>();
        Arrays.asList(allControllerAddressStr.split(",")).stream().forEach(endpoint -> {
            endpoints.add(endpoint.split(":")[0] + ":" + (Integer.parseInt(endpoint.split(":")[1]) - 2));
        });
        this.allElectionAddressStr = endpoints.stream().collect(Collectors.joining(","));
    }

    public int getTaskAccessThreadPoolNums() {
        return taskAccessThreadPoolNums;
    }

    public void setTaskAccessThreadPoolNums(int taskAccessThreadPoolNums) {
        this.taskAccessThreadPoolNums = taskAccessThreadPoolNums;
    }

    public int getSchedulerManagerThreadPoolNums() {
        return schedulerManagerThreadPoolNums;
    }

    public void setSchedulerManagerThreadPoolNums(int schedulerManagerThreadPoolNums) {
        this.schedulerManagerThreadPoolNums = schedulerManagerThreadPoolNums;
    }

    public String getConfigStorePath() {
        return configStorePath;
    }

    public void setConfigStorePath(final String configStorePath) {
        this.configStorePath = configStorePath;
    }

    public String getRiceHome() {
        return riceHome;
    }

    public void setRiceHome(String riceHome) {
        this.riceHome = riceHome;
    }

    public int getControllerPort() {
        try {
            if (controllerPort < 0) {
                String allControllerAddressStr = getAllControllerAddressStr();
                Stream<String> stringStream = Arrays.stream(allControllerAddressStr.split(",")).filter(item -> {
                    return item.contains(getLocalHost());
                });
                controllerPort = Integer.parseInt(stringStream.findFirst().get().split(":")[1]);
            }
        } catch (Exception e) {
            log.error("parse controller port error:", e);
        }
        return controllerPort;
    }

    public void setControllerPort(int controllerPort) {
        this.controllerPort = controllerPort;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getAllElectionAddressStr() {
        return allElectionAddressStr;
    }

    public int getManagePort() {
        return managePort;
    }

    public void setManagePort(int managePort) {
        this.managePort = managePort;
    }
}
