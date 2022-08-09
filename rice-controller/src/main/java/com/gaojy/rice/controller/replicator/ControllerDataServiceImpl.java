package com.gaojy.rice.controller.replicator;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.closure.ReadIndexClosure;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.util.BytesUtil;
import com.gaojy.rice.common.RiceThreadFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName ControllerDataServiceImpl.java
 * @Description
 * @createTime 2022/08/04 20:09:00
 */
public class ControllerDataServiceImpl implements ControllerDataService {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerDataServiceImpl.class);

    private final ReplicatorServer replicatorServer;

    private final Executor readIndexExecutor;

    public ControllerDataServiceImpl(ReplicatorServer replicatorServer) {
        this.replicatorServer = replicatorServer;
        this.readIndexExecutor = Executors.newCachedThreadPool(new RiceThreadFactory("readIndexExecutor_"));
    }

    private boolean isLeader() {
        return this.replicatorServer.getFsm().isLeader();
    }

    @Override
    public List<Map<String, String>> querySchedulersData(final boolean readOnlySafe, final ControllerClosure closure) {
        final List<Map<String, String>> data = new ArrayList<>();

        if (!readOnlySafe) {   // 不是顺序一致性读
            data.addAll(getValue());
            return data;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.replicatorServer.getNode().readIndex(BytesUtil.EMPTY_BYTES, new ReadIndexClosure() {
            // 这部分是异步的
            @Override
            public void run(Status status, long index, byte[] reqCtx) {
                if (status.isOk()) {
                    data.addAll(ControllerDataServiceImpl.this.getValue());
                }
                // 如果是leader 后台触发一次集群复制
                ControllerDataServiceImpl.this.readIndexExecutor.execute(() -> {
                    if (isLeader()) {
                        LOG.debug("Fail to get value with 'ReadIndex': {}, try to applying to the state machine.", status);
                        applyOperation(ControllerOperation.createGet(), closure);
                    }
                });
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("timeout exception", e);
        }
        return data;
    }

    private List<Map<String, String>> getValue() {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        this.replicatorServer.getFsm().getData().values().stream().forEach(data -> {
            list.add(data.getMap());
        });
        return list;
    }

    @Override
    public void updateSchedulerData(String schedulerAddress, SchedulerData data, final ControllerClosure closure) {

        applyOperation(ControllerOperation.createUpdate(schedulerAddress, data), closure);
    }

    private void applyOperation(final ControllerOperation op, final ControllerClosure closure) {
        if (!isLeader()) {
            LOG.error("Not Leader.");
            return;
        }

        try {
            closure.setControllerOperation(op);
            final Task task = new Task();
            task.setData(ByteBuffer.wrap(SerializerManager.getSerializer(SerializerManager.Hessian2).serialize(op)));
            task.setDone(closure);
            this.replicatorServer.getNode().apply(task);
        } catch (CodecException e) {
            String errorMsg = "Fail to encode CounterOperation";
            LOG.error(errorMsg, e);
        }
    }
}
