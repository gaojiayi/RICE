package com.gaojy.rice.dispatcher.config;

import com.gaojy.rice.common.constants.ElectionConstants;
import com.gaojy.rice.common.utils.MixAll;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaojy
 * @ClassName DispatcherConfig.java
 * @Description
 * @createTime 2022/02/09 20:28:00
 */
public class DispatcherConfig extends ElectionConstants {
    private String allControllerAddressStr = "";
    private String allControllerElectionAddressStr = "";
    private int JMXManagePort = 9090;

    private String riceHome = System.getProperty(MixAll.RICE_DISPATCHER_HOME_PROPERTY, System.getenv(MixAll.RICE_HOME_ENV));

    private String configStorePath = System.getProperty("user.home") + File.separator + "rice" + File.separator + "rice-controller.properties";

    public String getAllControllerAddressStr() {
        return allControllerAddressStr;
    }

    public void setAllControllerAddressStr(String allControllerAddressStr) {
        this.allControllerAddressStr = allControllerAddressStr;
        final List<String> confServerList = new ArrayList<>();
        /* raft 端口默认是业务端口  -2 */
        Arrays.asList(allControllerAddressStr.split(",")).stream().forEach(server -> {
            confServerList.add(server.split(":")[0] + ":" + (Integer.parseInt(server.split(":")[1]) - 2));
        });
        this.allControllerElectionAddressStr = confServerList.stream().collect(Collectors.joining(","));
    }

    public int getJMXManagePort() {
        return JMXManagePort;
    }

    public void setJMXManagePort(int JMXManagePort) {
        this.JMXManagePort = JMXManagePort;
    }

    public String getRiceHome() {
        return riceHome;
    }

    public void setRiceHome(String riceHome) {
        this.riceHome = riceHome;
    }

    public String getConfigStorePath() {
        return configStorePath;
    }

    public void setConfigStorePath(String configStorePath) {
        this.configStorePath = configStorePath;
    }

    public String getAllControllerElectionAddressStr() {
        return allControllerElectionAddressStr;
    }
}
