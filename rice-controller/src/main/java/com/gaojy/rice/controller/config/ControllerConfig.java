package com.gaojy.rice.controller.config;

import com.gaojy.rice.common.constants.ElectionConstants;

/**
 * @author gaojy
 * @ClassName ControllerConfig.java
 * @Description TODO
 * @createTime 2022/01/13 22:18:00
 */
public class ControllerConfig extends ElectionConstants {

    private int controller_port = 9090;

    private int controller_election_port = controller_port + 10;

    private int controller_console_port = controller_port + 5;

    private String electionDataPath = "/tmp/rice/controller/election";

    private String allControllerAddressStr = "";

    private String localHost = "127.0.0.1";

    private int taskAccessThreadPoolNums = 16;

    private int schedulerManagerThreadPoolNums = 16 + Runtime.getRuntime().availableProcessors() * 2;

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public int getController_port() {
        return controller_port;
    }

    public void setController_port(int controller_port) {
        this.controller_port = controller_port;
    }

    public int getController_election_port() {
        return controller_election_port;
    }

    public void setController_election_port(int controller_election_port) {
        this.controller_election_port = controller_election_port;
    }

    public String getElectionDataPath() {
        return electionDataPath;
    }

    public void setElectionDataPath(String electionDataPath) {
        this.electionDataPath = electionDataPath;
    }

    public String getAllControllerAddressStr() {
        return allControllerAddressStr;
    }

    public void setAllControllerAddressStr(String allControllerAddressStr) {
        this.allControllerAddressStr = allControllerAddressStr;
    }

    public int getController_console_port() {
        return controller_console_port;
    }

    public void setController_console_port(int controller_console_port) {
        this.controller_console_port = controller_console_port;
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
}
