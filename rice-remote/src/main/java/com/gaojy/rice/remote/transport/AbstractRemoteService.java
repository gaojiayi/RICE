package com.gaojy.rice.remote.transport;

import com.gaojy.rice.common.BackgroundThread;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.remote.ChannelEventListener;
import com.gaojy.rice.remote.InvokeCallback;
import com.gaojy.rice.remote.common.Pair;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.common.SemaphoreReleaseOnlyOnce;
import com.gaojy.rice.remote.common.TransfUtil;
import com.gaojy.rice.remote.protocol.RemotingSysResponseCode;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

    protected Pair<RiceRequestProcessor, ExecutorService> defaultRequestProcessor;

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
    class NettyChannelReceivedHandler extends SimpleChannelInboundHandler<RiceRemoteContext> {

        @Override
        protected void channelRead0(ChannelHandlerContext cxt, RiceRemoteContext message) throws Exception {
//            if (AbstractRemoteService.this.getClass().getSimpleName().toUpperCase().indexOf("server") > 0) {
//                // 获取client的PID
//                // 保存到channelTables  k = ip:pid  , v =  channel
//            }

            processMessageReceived(cxt, message);

        }
    }

    public void processMessageReceived(ChannelHandlerContext ctx, RiceRemoteContext msg) throws Exception {
        final RiceRemoteContext cmd = msg;
        if (cmd != null) {
            switch (cmd.getType()) {
                case REQUEST_COMMAND:
                    processRequestCommand(ctx, cmd);
                    break;
                case RESPONSE_COMMAND:
                    processResponseCommand(ctx, cmd);
                    break;
                default:
                    break;
            }
        }
    }

    public void processResponseCommand(ChannelHandlerContext ctx, RiceRemoteContext cmd) {
        final int opaque = cmd.getOpaque();
        final ResponseFuture responseFuture = responseTable.get(opaque);
        if (responseFuture != null) {
            responseFuture.setResponseCommand(cmd);

            responseFuture.release();

            responseTable.remove(opaque);

            if (responseFuture.getInvokeCallback() != null) {
                executeInvokeCallback(responseFuture);
            } else {
                responseFuture.putResponse(cmd);
            }
        } else {
            log.warn("receive response, but not matched any request, " + RemoteHelper.parseChannelRemoteAddr(ctx.channel()));
            log.warn(cmd.toString());
        }
    }

    public void processRequestCommand(final ChannelHandlerContext ctx, final RiceRemoteContext cmd) {
        // 根据请求code，获取已经注册的处理器与线程池配对组
        final Pair<RiceRequestProcessor, ExecutorService> matched = this.processorTable.get(cmd.getCode());
        // 如果找不到 则使用默认处理器  在server端使用adminprocess作为默认处理器
        final Pair<RiceRequestProcessor, ExecutorService> pair = null == matched ? this.defaultRequestProcessor : matched;
        final int opaque = cmd.getOpaque();

        if (pair != null) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        // 处理请求的具体逻辑，返回RemotingCommand响应数据
                        final RiceRemoteContext response = pair.getObject1().processRequest(ctx, cmd);
                        // 对于远程过来的请求类型不是单向请求的话  则设置opaque  ResponseType
                        if (!cmd.isOnewayRPC()) {
                            if (response != null) {
                                response.setOpaque(opaque);
                                response.markResponseType();
                                try {
                                    ctx.writeAndFlush(response);
                                } catch (Throwable e) {
                                    log.error("process request over, but response failed", e);
                                    log.error(cmd.toString());
                                    log.error(response.toString());
                                }
                            } else {

                            }
                        }
                    } catch (Throwable e) {
                        log.error("process request exception", e);
                        log.error(cmd.toString());

                        if (!cmd.isOnewayRPC()) {
                            final RiceRemoteContext response = RiceRemoteContext.createResponseCommand(RemotingSysResponseCode.SYSTEM_ERROR, //
                                RemoteHelper.exceptionSimpleDesc(e));
                            response.setOpaque(opaque);
                            ctx.writeAndFlush(response);
                        }
                    }
                }
            };

            if (pair.getObject1().rejectRequest()) {
                final RiceRemoteContext response = RiceRemoteContext.createResponseCommand(RemotingSysResponseCode.SYSTEM_BUSY,
                    "[REJECTREQUEST]system busy, start flow control for a while");
                response.setOpaque(opaque);
                ctx.writeAndFlush(response);
                return;
            }

            try {
                // 把任务包装成RequestTask  submit给对应的线程池
                final RequestTask requestTask = new RequestTask(run, ctx.channel(), cmd);
                pair.getObject2().submit(requestTask);
            } catch (RejectedExecutionException e) {
                if ((System.currentTimeMillis() % 10000) == 0) {
                    log.warn(RemoteHelper.parseChannelRemoteAddr(ctx.channel()) //
                        + ", too many requests and system thread pool busy, RejectedExecutionException " //
                        + pair.getObject2().toString() //
                        + " request code: " + cmd.getCode());
                }

                if (!cmd.isOnewayRPC()) {
                    final RiceRemoteContext response = RiceRemoteContext.createResponseCommand(RemotingSysResponseCode.SYSTEM_BUSY,
                        "[OVERLOAD]system busy, start flow control for a while");
                    response.setOpaque(opaque);
                    ctx.writeAndFlush(response);
                }
            }
        } else {
            String error = " request type " + cmd.getCode() + " not supported";
            final RiceRemoteContext response =
                RiceRemoteContext.createResponseCommand(RemotingSysResponseCode.REQUEST_CODE_NOT_SUPPORTED, error);
            response.setOpaque(opaque);
            ctx.writeAndFlush(response);
            log.error(RemoteHelper.parseChannelRemoteAddr(ctx.channel()) + error);
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
        private final ChannelFuture channelFuture;

        public ChannelWrapper(Channel channel) {
            this.channel = channel;
            channelFuture = null;
        }

        public ChannelWrapper(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
            this.channel = channelFuture.channel();
        }

        public boolean isActive() {
            return this.channel != null && this.channel.isActive();
        }

        public Channel getChannel() {
            return this.channel;
        }

        public ChannelFuture getChannelFuture() {
            return channelFuture;
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

    public RiceRemoteContext invokeSyncImpl(final Channel channel, final RiceRemoteContext request,
        final long timeoutMillis)
        throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException {
        final int opaque = request.getOpaque();

        try {
            final ResponseFuture responseFuture = new ResponseFuture(opaque, timeoutMillis, null, null);
            this.responseTable.put(opaque, responseFuture);
            final SocketAddress addr = channel.remoteAddress();
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    if (f.isSuccess()) {
                        responseFuture.setSendRequestOK(true);
                        return;
                    } else {
                        responseFuture.setSendRequestOK(false);
                    }

                    responseTable.remove(opaque);
                    responseFuture.setCause(f.cause());
                    responseFuture.putResponse(null);
                    log.warn("send a request command to channel <" + addr + "> failed.");
                }
            });

            RiceRemoteContext responseCommand = responseFuture.waitResponse(timeoutMillis);
            if (null == responseCommand) {
                if (responseFuture.isSendRequestOK()) {
                    throw new RemotingTimeoutException(RemoteHelper.parseSocketAddressAddr(addr), timeoutMillis,
                        responseFuture.getCause());
                } else {
                    throw new RemotingSendRequestException(RemoteHelper.parseSocketAddressAddr(addr), responseFuture.getCause());
                }
            }

            return responseCommand;
        } finally {
            this.responseTable.remove(opaque);
        }
    }


    public void invokeAsyncImpl(final Channel channel, final RiceRemoteContext request, final long timeoutMillis,
        final InvokeCallback invokeCallback)
        throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException {
        final int opaque = request.getOpaque();
        boolean acquired = this.semaphoreAsync.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        if (acquired) {
            final SemaphoreReleaseOnlyOnce once = new SemaphoreReleaseOnlyOnce(this.semaphoreAsync);

            final ResponseFuture responseFuture = new ResponseFuture(opaque, timeoutMillis, invokeCallback, once);
            this.responseTable.put(opaque, responseFuture);
            try {
                channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture f) throws Exception {
                        if (f.isSuccess()) {
                            responseFuture.setSendRequestOK(true);
                            return;
                        } else {
                            responseFuture.setSendRequestOK(false);
                        }

                        responseFuture.putResponse(null);
                        responseTable.remove(opaque);
                        try {
                            /**
                             * 异步调用失败   仍然会执行回调函数
                             */
                            executeInvokeCallback(responseFuture);
                        } catch (Throwable e) {
                            log.warn("excute callback in writeAndFlush addListener, and callback throw", e);
                        } finally {
                            responseFuture.release();
                        }

                        log.warn("send a request command to channel <{}> failed.", RemoteHelper.parseChannelRemoteAddr(channel));
                    }
                });
            } catch (Exception e) {
                responseFuture.release();
                log.warn("send a request command to channel <" + RemoteHelper.parseChannelRemoteAddr(channel) + "> Exception", e);
                throw new RemotingSendRequestException(RemoteHelper.parseChannelRemoteAddr(channel), e);
            }
        } else {
            if (timeoutMillis <= 0) {
                throw new RemotingTooMuchRequestException("invokeAsyncImpl invoke too fast");
            } else {
                String info =
                    String.format("invokeAsyncImpl tryAcquire semaphore timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d", //
                        timeoutMillis, //
                        this.semaphoreAsync.getQueueLength(), //
                        this.semaphoreAsync.availablePermits()//
                    );
                log.warn(info);
                throw new RemotingTimeoutException(info);
            }
        }
    }

    public void invokeOnewayImpl(final Channel channel, final RiceRemoteContext request, final long timeoutMillis)
        throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException {
        request.markOnewayRPC();
        boolean acquired = this.semaphoreOneway.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        if (acquired) {
            final SemaphoreReleaseOnlyOnce once = new SemaphoreReleaseOnlyOnce(this.semaphoreOneway);
            try {
                channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture f) throws Exception {
                        once.release();
                        if (!f.isSuccess()) {
                            log.warn("send a request command to channel <" + channel.remoteAddress() + "> failed.");
                        }
                    }
                });
            } catch (Exception e) {
                once.release();
                log.warn("write send a request command to channel <" + channel.remoteAddress() + "> failed.");
                throw new RemotingSendRequestException(RemoteHelper.parseChannelRemoteAddr(channel), e);
            }
        } else {
            if (timeoutMillis <= 0) {
                throw new RemotingTooMuchRequestException("invokeOnewayImpl invoke too fast");
            } else {
                String info = String.format(
                    "invokeOnewayImpl tryAcquire semaphore timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d", //
                    timeoutMillis, //
                    this.semaphoreOneway.getQueueLength(), //
                    this.semaphoreOneway.availablePermits()//
                );
                log.warn(info);
                throw new RemotingTimeoutException(info);
            }
        }
    }

}
