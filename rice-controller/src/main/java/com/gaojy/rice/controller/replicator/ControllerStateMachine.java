package com.gaojy.rice.controller.replicator;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.error.RaftException;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotReader;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;
import com.gaojy.rice.common.utils.StringUtil;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gaojy.rice.controller.replicator.ControllerOperation.GET;
import static com.gaojy.rice.controller.replicator.ControllerOperation.UPDATE;

/**
 * @author gaojy
 * @ClassName ControllerStateMachine.java
 * @Description 控制器状态机，在控制器集群中通过复制保证数据一致性
 * @createTime 2022/07/31 23:42:00
 */
public class ControllerStateMachine extends StateMachineAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerStateMachine.class);
    private final List<LeaderStateListener> listeners;

    public ControllerStateMachine(List<LeaderStateListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * @description 保存调度器的数据
     */
    private final ConcurrentMap<String /* scheduler address */, SchedulerData> data = new ConcurrentHashMap<>();

    /**
     * Leader term
     */
    private final AtomicLong leaderTerm = new AtomicLong(-1);

    public boolean isLeader() {
        return this.leaderTerm.get() > 0;
    }

    //   raft 日志  变成业务逻辑结果  。node-》apply-》task之后执行
    @Override
    public void onApply(Iterator iterator) {

        while (iterator.hasNext()) {
            ControllerOperation controllerOperation = null;
            ControllerClosure closure = null;
            if (iterator.done() != null) {
                closure = (ControllerClosure) iterator.done();
                controllerOperation = closure.getControllerOperation();
            } else {
                final ByteBuffer data = iterator.getData();
                try {
                    controllerOperation = SerializerManager.getSerializer(SerializerManager.Hessian2).deserialize(
                        data.array(), ControllerOperation.class.getName());
                } catch (final CodecException e) {
                    LOG.error("Fail to decode IncrementAndGetRequest", e);
                }
            }

            if (controllerOperation != null) {
                switch (controllerOperation.getOp()) {
                    case GET:
                        LOG.info("Get value={} at logIndex={}", getData().toString(), iterator.getIndex());
                        break;
                    case UPDATE:
                        String schedulerAddress = controllerOperation.getSchedulerAddress();
                        SchedulerData schedulerData = controllerOperation.getSchedulerData();
                        if (StringUtil.isNotEmpty(schedulerAddress) && schedulerData != null) {
                            this.data.put(schedulerAddress, schedulerData);
                        }
                        break;
                }
                if (closure != null) {
                    closure.run(Status.OK());
                }
            }
            iterator.next();
        }

    }

    public ConcurrentMap<String, SchedulerData> getData() {
        return data;
    }
    @Override
    public void onError(final RaftException e) {
        LOG.error("Raft error: {}", e, e);
    }

    @Override
    public void onLeaderStart(long term) {
        this.leaderTerm.set(term);
        super.onLeaderStart(term);
    }


    @Override
    public void onLeaderStop(Status status) {
        this.leaderTerm.set(-1);
        super.onLeaderStop(status);
    }

    /* 对调度器的服务器监控信息 展示不使用快照存储 */
    @Override
    public void onSnapshotSave(SnapshotWriter writer, Closure done) {
        super.onSnapshotSave(writer, done);
    }

    @Override
    public boolean onSnapshotLoad(SnapshotReader reader) {
        return super.onSnapshotLoad(reader);
    }
}
