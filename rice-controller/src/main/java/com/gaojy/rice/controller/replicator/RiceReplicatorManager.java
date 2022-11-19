package com.gaojy.rice.controller.replicator;

import com.alipay.sofa.jraft.RaftGroupService;
import com.gaojy.rice.controller.config.ControllerConfig;

/**
 * @author gaojy
 * @ClassName RiceReplicatorManager.java
 * @Description
 * @createTime 2022/08/09 16:31:00
 */
public class RiceReplicatorManager {
    private final ReplicatorServer server;
    private final ReplicatorNodeOptions opts = new ReplicatorNodeOptions();

    public RiceReplicatorManager(final ControllerConfig config, LeaderStateListener listener) {
        server = new ReplicatorServer();
        server.addLeaderStateListener(listener);
        opts.setDataPath(config.getDataPath());
        opts.setGroupId(config.ELECTION_GROUP_ID);
        opts.setInitialServerAddressList(config.getAllElectionAddressStr());
        opts.setServerAddress(config.getLocalHost() + ":" + (config.getControllerPort() - 2));
    }

    public void start() {
        server.init(opts);
    }

    public Boolean isLeader() {
        return server.getNode().isLeader();
    }

    public String getLeader() {
        return server.getNode().getLeaderId().getIp() + ":" +
            (server.getNode().getLeaderId().getPort() + 2);
    }

    public void stop() {
        server.shutdown();
    }

    public ReplicatorServer getServer() {
        return server;
    }
}
