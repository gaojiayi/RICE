package com.gaojy.rice.controller.replicator;

import com.alipay.sofa.jraft.Lifecycle;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import com.alipay.sofa.jraft.rpc.RpcServer;
import com.alipay.sofa.jraft.util.internal.ThrowUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName ReplicatorServer.java
 * @Description 分布式主从数据同步服务
 * rice的控制器主要存储各个调度器的状态信息,数据的查询不采用顺序一致性读
 * @createTime 2022/08/01 15:14:00
 */
public class ReplicatorServer implements Lifecycle<ReplicatorNodeOptions> {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicatorServer.class);

    private RaftGroupService raftGroupService;
    private Node node;
    private ControllerStateMachine fsm;
    private final List<LeaderStateListener> listeners = new CopyOnWriteArrayList<>();

    public ReplicatorServer(final String dataPath, final String groupId, final PeerId serverId,
        final NodeOptions nodeOptions) throws IOException {
        // 初始化路径
        FileUtils.forceMkdir(new File(dataPath));
        // 这里让 raft RPC 和业务 RPC 使用同一个 RPC server, 通常也可以分开
        final RpcServer rpcServer = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint());
        // 初始化状态机
        this.fsm = new ControllerStateMachine(this.listeners);
        // 设置状态机到启动参数
        nodeOptions.setFsm(this.fsm);
        // 设置存储路径
        // 日志, 必须
        nodeOptions.setLogUri(dataPath + File.separator + "log");
        // 元信息, 必须
        nodeOptions.setRaftMetaUri(dataPath + File.separator + "raft_meta");
        // snapshot, 可选, 一般都推荐
        nodeOptions.setSnapshotUri(dataPath + File.separator + "snapshot");
        // 初始化 raft group 服务框架
        this.raftGroupService = new RaftGroupService(groupId, serverId, nodeOptions, rpcServer);
        // 启动
        this.node = this.raftGroupService.start();
    }

    public ReplicatorServer() {
    }

    public PeerId redirect() {
        if (this.node != null) {
            final PeerId leader = this.node.getLeaderId();
            if (leader != null) {
                return leader;
            }
        }
        return null;
    }

    public ControllerStateMachine getFsm() {
        return this.fsm;
    }

    public Node getNode() {
        return this.node;
    }

    public void addLeaderStateListener(final LeaderStateListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public boolean init(ReplicatorNodeOptions opts) {
        try {
            // 初始化路径
            FileUtils.forceMkdir(new File(opts.getDataPath()));
            final PeerId serverId = new PeerId();
            if (!serverId.parse(opts.getServerAddress())) {
                throw new IllegalArgumentException("Fail to parse serverId: " + opts.getServerAddress());
            }
            // 这里让 raft RPC 和业务 RPC 使用同一个 RPC server, 通常也可以分开
            final RpcServer rpcServer = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint());
            // 初始化状态机
            this.fsm = new ControllerStateMachine(this.listeners);
            // node options
            NodeOptions nodeOptions = opts.getNodeOptions();
            Configuration initConf = new Configuration();
            if (!initConf.parse(opts.getInitialServerAddressList())) {
                throw new IllegalArgumentException("Fail to parse initConf:" + initConf);
            }

            if (nodeOptions == null) {
                nodeOptions = new NodeOptions();
            }
            // 设置状态机到启动参数
            nodeOptions.setFsm(this.fsm);
            // 设置存储路径
            // 日志, 必须
            nodeOptions.setLogUri(opts.getDataPath() + File.separator + "log");
            // 元信息, 必须
            nodeOptions.setRaftMetaUri(opts.getDataPath() + File.separator + "raft_meta");
            // snapshot, 可选, 一般都推荐
            nodeOptions.setSnapshotUri(opts.getDataPath() + File.separator + "snapshot");
            nodeOptions.setInitialConf(initConf);
            // 初始化 raft group 服务框架
            this.raftGroupService = new RaftGroupService(opts.getGroupId(), serverId, nodeOptions, rpcServer);
            this.node = this.raftGroupService.start();
            LOG.info("ReplicatorServer started successfully");
            return true;
        } catch (IOException e) {
            LOG.error("init ReplicatorServer failed,error=", e);
        }
        return false;
    }

    @Override
    public void shutdown() {
        if (this.raftGroupService != null) {
            this.raftGroupService.shutdown();
            try {
                this.raftGroupService.join();
            } catch (final InterruptedException e) {
                ThrowUtil.throwException(e);
            }
        }
        LOG.info("[ReplicatorServer] shutdown successfully: {}.", this);

    }
}
