package com.gaojy.rice.remote.transport;

import com.gaojy.rice.common.BackgroundThread;
import com.gaojy.rice.remote.ChannelEventListener;
import com.gaojy.rice.remote.common.Pair;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.common.TransfUtil;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName AbstractRemoteService.java
 * @Description 作为RPC服务端和客户端的基础类
 * @createTime 2022/01/01 13:40:00
 */
public abstract class AbstractRemoteService {

    private static final Logger log = LoggerFactory.getLogger(RemoteHelper.RICE_REMOTING);

    private final Lock lockChannelTables = new ReentrantLock();
    private static final long LOCK_TIMEOUT_MILLIS = 3000;

    public abstract ChannelEventListener getChannelEventListener();

    private final ConcurrentMap<String /* addr */, ChannelWrapper> channelTables = new ConcurrentHashMap<String, ChannelWrapper>();
    private final Timer timer = new Timer("RemoteHouseKeepingService", true);
    private ExecutorService publicExecutor;

    protected final Semaphore semaphoreOneway;
    protected final Semaphore semaphoreAsync;

    /**
     * @description 响应对象容器  可并发迭代
     */
    protected final ConcurrentMap<Integer /* opaque */, ResponseFuture> responseTable =
        new ConcurrentHashMap<Integer, ResponseFuture>(256);

    /**
     * @description request code 与 对应的处理器即处理器执行线程池映射关系
     */
    protected final HashMap<Integer/* request code */, Pair<RiceRequestProcessor, ExecutorService>> processorTable =
        new HashMap<Integer, Pair<RiceRequestProcessor, ExecutorService>>(64);

    /**
     * 用于处理网络事件
     */
    protected final NettyEventExecutor nettyEventExecutor = new NettyEventExecutor();

    public AbstractRemoteService(final int permitsOneway, final int permitsAsync) {
        this.semaphoreOneway = new Semaphore(permitsOneway, true);
        this.semaphoreAsync = new Semaphore(permitsAsync, true);
    }

    public ConcurrentMap<String, ChannelWrapper> getChannelTables() {
        return channelTables;
    }

    public Timer getTimer() {
        return timer;
    }

    public ExecutorService getPublicExecutor() {
        return publicExecutor;
    }

    public void setPublicExecutor(ExecutorService publicExecutor) {
        this.publicExecutor = publicExecutor;
    }

    /**
     * @description 不管是客户端还是服务端，接收数据的处理逻辑都是NettyChannelReadHandler处理
     */
    class NettyChannelReadHandler extends SimpleChannelInboundHandler<RiceRemoteContext> {

        @Override protected void channelRead0(ChannelHandlerContext cxt, RiceRemoteContext message) throws Exception {
            if (AbstractRemoteService.this.getClass().getSimpleName().toUpperCase().indexOf("server") > 0) {
                // 获取client的PID
                // 保存到channelTables  k = ip:pid  , v =  channel
            }

        }
    }

    public void registerProcessor(int requestCode, RiceRequestProcessor processor, ExecutorService executor) {
        ExecutorService executorThis = executor;
        if (null == executor) {
            executorThis = this.publicExecutor;
        }

        Pair<RiceRequestProcessor, ExecutorService> pair = new Pair<RiceRequestProcessor, ExecutorService>(processor, executorThis);
        this.processorTable.put(requestCode, pair);
    }

    private void executeInvokeCallback(final ResponseFuture responseFuture) {
        boolean runInThisThread = false;
        ExecutorService executor = this.publicExecutor;
        if (executor != null) {
            try {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            responseFuture.executeInvokeCallback();
                        } catch (Throwable e) {
                            log.warn("execute callback in executor exception, and callback throw", e);
                        }
                    }
                });
            } catch (Exception e) {
                runInThisThread = true;
                log.warn("execute callback in executor exception, maybe executor busy", e);
            }
        } else {
            runInThisThread = true;
        }

        if (runInThisThread) {
            try {
                responseFuture.executeInvokeCallback();
            } catch (Throwable e) {
                log.warn("executeInvokeCallback Exception", e);
            }
        }
    }

    /**
     * @description 响应对象的维护
     */
    public void scanResponseTable() {
        final List<ResponseFuture> waitToRemoveList = new LinkedList<ResponseFuture>();
        Iterator<Map.Entry<Integer, ResponseFuture>> it = this.responseTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ResponseFuture> next = it.next();
            ResponseFuture rep = next.getValue();
            // 停止等待超时响应 并删除缓存
            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + 1000) <= System.currentTimeMillis()) {
                rep.release();
                it.remove();
                waitToRemoveList.add(rep);
                log.warn("remove timeout request, " + rep);
            }
        }
        waitToRemoveList.stream().forEach(response -> {
            try {
                executeInvokeCallback(response);
            } catch (Exception e) {
                log.warn("scanResponseTable, operationComplete Exception", e);
            }
        });

    }

    class ChannelWrapper {
        private final Channel channel;

        public ChannelWrapper(Channel channel) {
            this.channel = channel;
        }

        public boolean isActive() {
            return this.channel != null && this.channel.isActive();
        }

        public Channel getChannel() {
            return this.channel;
        }

    }

    class NettyEventExecutor extends BackgroundThread {

        private final LinkedBlockingQueue<NettyEvent> eventQueue = new LinkedBlockingQueue<NettyEvent>();
        private final int maxSize = 10000;

        public void triggerNettyEvent(final NettyEvent event) {
            if (this.eventQueue.size() <= maxSize) {
                this.eventQueue.add(event);
            } else {
                log.warn("event queue size[{}] enough, so drop this event {}", this.eventQueue.size(), event.toString());
            }

        }

        @Override
        public String getServiceName() {
            return NettyEventExecutor.class.getSimpleName();
        }

        @Override
        public void run() {
            log.info(this.getServiceName() + " service started");

            final ChannelEventListener listener = AbstractRemoteService.this.getChannelEventListener();

            while (!this.isStopped()) {
                try {
                    NettyEvent event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (event != null && listener != null) {
                        switch (event.getType()) {
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddr(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddr(), event.getChannel());
                                break;
                            default:
                                break;

                        }
                    }
                } catch (Exception e) {
                    log.warn(this.getServiceName() + " service has exception. ", e);
                }
            }

            log.info(this.getServiceName() + " service end");
        }
    }

    public void addChannel(final String addr, final Channel channel) {
        if (null == channel || addr == null)
            return;

        try {
            ChannelWrapper channelWrapper = new ChannelWrapper(channel);
            ChannelWrapper prevCW = null;
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                prevCW = channelTables.putIfAbsent(addr, channelWrapper);
                log.info("addChannel: the channel[{}] was added to channel table", addr);
                if (prevCW != null && !prevCW.isActive()) {
                    channelTables.put(addr, channelWrapper);
                    TransfUtil.closeChannel(prevCW.getChannel());
                }

                if (prevCW != null && prevCW.isActive()) {
                    log.warn("addChannel: the channel[{}] has exist in channel table and close new channel", addr);
                    TransfUtil.closeChannel(channel);
                }
            }
        } catch (InterruptedException e) {
            log.error("addChannel: add the channel exception", e);
        } finally {
            this.lockChannelTables.unlock();
        }
    }

    public void closeChannel(final Channel channel) {
        if (null == channel)
            return;

        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    ChannelWrapper prevCW = null;
                    String addrRemote = null;
                    for (Map.Entry<String, ChannelWrapper> entry : channelTables.entrySet()) {
                        String key = entry.getKey();
                        ChannelWrapper prev = entry.getValue();
                        if (prev.getChannel() != null) {
                            if (prev.getChannel() == channel) {
                                prevCW = prev;
                                addrRemote = key;
                                break;
                            }
                        }
                    }

                    if (null == prevCW) {
                        log.info("eventCloseChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    }

                    if (removeItemFromTable) {
                        this.channelTables.remove(addrRemote);
                        log.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                    }
                    TransfUtil.closeChannel(channel);
                } catch (Exception e) {
                    log.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                log.warn("closeChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
            log.error("closeChannel exception", e);
        }
    }

    public void shutdown() {

        this.timer.cancel();
        for (ChannelWrapper cw : this.channelTables.values()) {
            this.closeChannel(cw.getChannel());
        }
        this.channelTables.clear();

        if (this.nettyEventExecutor != null) {
            this.nettyEventExecutor.shutdown();
        }

        if (this.publicExecutor != null) {
            this.publicExecutor.shutdown();
        }

    }

}
