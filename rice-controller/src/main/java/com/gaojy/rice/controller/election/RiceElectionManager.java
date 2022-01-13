package com.gaojy.rice.controller.election;

import com.alipay.sofa.jraft.entity.PeerId;
import com.gaojy.rice.controller.config.ControllerConfig;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * @author gaojy
 * @ClassName RiceElecManager.java
 * @Description RICE 选举管理器  主控制器负责任务的重新分配分配
 * @createTime 2022/01/13 21:56:00
 */
public class RiceElectionManager {
    final ElectionNode node = new ElectionNode();
    final ElectionNodeOptions electionOpts = new ElectionNodeOptions();
    final LeaderStateListener listener;
    /**
     * 选举信息保存位置
     */
    final String dataPath;

    /**
     * 选举组名
     */
    final String groupId;

    /**
     * 本地IP:选举端口
     */
    final String serverIdStr;

    /**
     * 所有控制器
     */
    final String initialConfStr;

    public RiceElectionManager(ControllerConfig controllerConfig, LeaderStateListener listener) {
        this.listener = listener;
        dataPath = controllerConfig.getElectionDataPath();
        groupId = ControllerConfig.ELECTION_GROUP_ID;
        serverIdStr = controllerConfig.getLocalHost() + ":" + controllerConfig.getController_election_port();
        boolean allMatch = Arrays.stream(controllerConfig.getAllControllerAddressStr().split(",")).allMatch(address -> {
            return address.split(":").length == 2;
        });
        if (allMatch) {
            initialConfStr = controllerConfig.getAllControllerAddressStr();
        } else {
            initialConfStr = controllerConfig.getAllControllerAddressStr().replaceAll(",",
                ":" + controllerConfig.getController_election_port() + ",")
                + ":" + controllerConfig.getController_election_port();
        }

    }

    public void start() {
        electionOpts.setDataPath(dataPath);
        electionOpts.setGroupId(groupId);
        electionOpts.setServerAddress(serverIdStr);
        electionOpts.setInitialServerAddressList(initialConfStr);
        node.addLeaderStateListener(this.listener);
        node.init(electionOpts);
    }

    public Boolean isLeader() {
        return node.isLeader();
    }

    public void stopElection() {
        node.shutdown();
    }

}
