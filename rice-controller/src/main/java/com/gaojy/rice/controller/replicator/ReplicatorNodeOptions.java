package com.gaojy.rice.controller.replicator;

import com.alipay.sofa.jraft.option.NodeOptions;

/**
 * @author gaojy
 * @ClassName ReplicatorNodeOptions.java
 * @Description
 * @createTime 2022/08/09 16:05:00
 */
public class ReplicatorNodeOptions {
    private String      dataPath;
    // raft group id
    private String      groupId;
    // ip:port
    private String      serverAddress;
    // ip:port,ip:port,ip:port
    private String      initialServerAddressList;
    // raft node options
    private NodeOptions nodeOptions;

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getInitialServerAddressList() {
        return initialServerAddressList;
    }

    public void setInitialServerAddressList(String initialServerAddressList) {
        this.initialServerAddressList = initialServerAddressList;
    }

    public NodeOptions getNodeOptions() {
        return nodeOptions;
    }

    public void setNodeOptions(NodeOptions nodeOptions) {
        this.nodeOptions = nodeOptions;
    }
}
