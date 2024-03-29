package com.gaojy.rice.remote.transport;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.remote.ChannelEventListener;
import com.gaojy.rice.remote.IBaseRemote;
import com.gaojy.rice.remote.InvokeCallback;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.common.TransfUtil;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import java.net.SocketAddress;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TransportClient.java
 * @Description
 * @createTime 2022/01/01 14:55:00
 */
public class TransportClient extends AbstractRemoteService implements IBaseRemote {
    private static final Logger log = LoggerFactory.getLogger(RemoteHelper.RICE_REMOTING);

    private final TransfClientConfig transfClientConfig;

    private static final long LOCK_TIMEOUT_MILLIS = 3000;

    /**
     * Netty Bootstrap
     */
    private final Bootstrap bootstrap = new Bootstrap();

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private final ChannelEventListener channelEventListener;

    // 负责selector就绪选择
    private final EventLoopGroup eventLoopGroupWorker;

    public TransportClient(TransfClientConfig transfClientConfig) {
        this(transfClientConfig, null);
    }

    public TransportClient(TransfClientConfig transfClientConfig,
        ChannelEventListener channelEventListener) {
        super(transfClientConfig.getClientOnewaySemaphoreValue(), transfClientConfig.getClientAsyncSemaphoreValue());
        this.transfClientConfig = transfClientConfig;
        this.channelEventListener = channelEventListener;
        this.eventLoopGroupWorker = new NioEventLoopGroup(1, new RiceThreadFactory("TransportClientSelector_"));
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(transfClientConfig.getClientWorkerThreads(),
            new RiceThreadFactory("TransportClientWorkerThread_"));

        int publicThreadNums = transfClientConfig.getClientCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }
        setPublicExecutor(Executors.newFixedThreadPool(publicThreadNums,
            new RiceThreadFactory("NettyClientPublicExecutor_")));
    }

    @Override
    public RiceRemoteContext invokeSync(String addr, RiceRemoteContext request, long timeoutMillis)
        throws RemotingConnectException, RemotingTimeoutException, RemotingSendRequestException, InterruptedException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                RiceRemoteContext response = this.invokeSyncImpl(channel, request, timeoutMillis);
                return response;
            } catch (RemotingSendRequestException e) {
                log.warn("invokeSync: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr,channel);
                throw e;
            } catch (RemotingTimeoutException e) {
                if (transfClientConfig.isClientCloseSocketIfTimeout()) {
                    this.closeChannel(addr,channel);
                    log.warn("invokeSync: close socket because of timeout, {}ms, {}", timeoutMillis, addr);
                }
                log.warn("invokeSync: wait response timeout exception, the channel[{}]", addr);
                throw e;
            }
        } else {
            this.closeChannel(addr,channel);
            throw new RemotingConnectException(addr);
        }

    }

    @Override
    public void invokeAsync(String addr, RiceRemoteContext request, long timeoutMillis,
        InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
        RemotingTooMuchRequestException, RemotingTimeoutException,
        RemotingSendRequestException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                this.invokeAsyncImpl(channel, request, timeoutMillis, invokeCallback);
            } catch (RemotingSendRequestException e) {
                log.warn("invokeAsync: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr,channel);
                throw e;
            }
        } else {
            this.closeChannel(addr,channel);
            throw new RemotingConnectException(addr);
        }

    }

    @Override
    public void invokeOneWay(String addr, RiceRemoteContext request,
        final long timeoutMillis) throws InterruptedException,
        RemotingConnectException, RemotingTooMuchRequestException,
        RemotingTimeoutException, RemotingSendRequestException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                this.invokeOnewayImpl(channel, request, timeoutMillis);
            } catch (RemotingSendRequestException e) {
                log.warn("invokeOneway: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr,channel);
                throw e;
            }
        } else {
            this.closeChannel(addr,channel);
            throw new RemotingConnectException(addr);
        }
    }

    private Channel getAndCreateChannel(final String addr) {
        ChannelWrapper cw = getChannelTables().get(addr);
        if (cw != null && cw.isActive()) {
            return cw.getChannel();
        }
        ChannelFuture channelFuture = this.bootstrap.connect(RemoteHelper.string2SocketAddress(addr));
        log.info("createChannel: begin to connect remote host[{}] asynchronously", addr);
        cw = new ChannelWrapper(channelFuture);
        getChannelTables().put(addr, cw);

        if (channelFuture.awaitUninterruptibly(this.transfClientConfig.getConnectTimeoutMillis())) {
            if (cw.isActive()) {
                log.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                return cw.getChannel();
            } else {
                log.warn("createChannel: connect remote host[" + addr + "] failed, " + channelFuture.toString(), channelFuture.cause());
            }
        } else {
            log.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, this.transfClientConfig.getConnectTimeoutMillis(),
                channelFuture.toString());
        }
        return channelFuture.channel();
    }

    @Override
    public void start() {

        this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)//
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.SO_KEEPALIVE, false)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, transfClientConfig.getConnectTimeoutMillis())
            .option(ChannelOption.SO_SNDBUF, transfClientConfig.getClientSocketSndBufSize())
            .option(ChannelOption.SO_RCVBUF, transfClientConfig.getClientSocketRcvBufSize())
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                        defaultEventExecutorGroup,
                        new NettyEncoder(TransportClient.this),
                        new NettyDecoder(TransportClient.this),
                        new IdleStateHandler(0, 0, transfClientConfig.getClientChannelMaxIdleTimeSeconds()),
                        // 网络事件处理器
                        new NettyClientConnectManageHandler(),
                        // 接收数据处理
                        new NettyChannelReceivedHandler());
                }
            });

        getTimer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    TransportClient.this.scanResponseTable();
                } catch (Throwable e) {
                    log.error("scanResponseTable exception", e);
                }
            }
        }, 1000 * 3, 1000);

        // 处理网络事件后台监听
        if (this.channelEventListener != null) {
            this.nettyEventExecutor.start();
        }
    }

    @Override
    public void shutdown() {
        try {
            super.shutdown();
            for (ChannelWrapper cw : this.channelTables.values()) {
                this.closeChannel(null,cw.getChannel());
            }
            this.eventLoopGroupWorker.shutdownGracefully();

            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }


        } catch (Exception e) {
            log.error("TransportClient shutdown exception, ", e);
        }
    }

    @Override
    public ChannelEventListener getChannelEventListener() {
        return channelEventListener;
    }


    public void closeChannel(final String addr, final Channel channel) {
        if (null == channel)
            return;

        final String addrRemote = null == addr ? RemoteHelper.parseChannelRemoteAddr(channel) : addr;

        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    final ChannelWrapper prevCW = this.channelTables.get(addrRemote);

                    log.info("closeChannel: begin close the channel[{}] Found: {}", addrRemote, prevCW != null);

                    if (null == prevCW) {
                        log.info("closeChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    } else if (prevCW.getChannel() != channel) {
                        log.info("closeChannel: the channel[{}] has been closed before, and has been created again, nothing to do.",
                            addrRemote);
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
                        TransfUtil.closeChannel(channel);
                    }

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

    /**
     * @description 监听到netty事件之后，把对应的时间放到nettyEventExecutor中的队列中。由后台线程负责调用channelEventListener
     */
    class NettyClientConnectManageHandler extends ChannelDuplexHandler {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY CLIENT PIPELINE: ACTIVE {}", remoteAddress);
            super.channelActive(ctx);

            if (TransportClient.this.channelEventListener != null) {
                TransportClient.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.ACTIVE, remoteAddress, ctx.channel()));
            }
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
            ChannelPromise future) throws Exception {
            final String local = localAddress == null ? "UNKNOWN" : RemoteHelper.parseSocketAddressAddr(localAddress);
            final String remote = remoteAddress == null ? "UNKNOWN" : RemoteHelper.parseSocketAddressAddr(remoteAddress);
            log.info("NETTY CLIENT PIPELINE: CONNECT  {} => {}", local, remote);
            super.connect(ctx, remoteAddress, localAddress, future);
            if (TransportClient.this.channelEventListener != null) {
                TransportClient.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.CONNECT, remote, ctx.channel()));
            }
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY CLIENT PIPELINE: DISCONNECT {}", remoteAddress);
            closeChannel(ctx.channel());
            super.disconnect(ctx, future);

            if (TransportClient.this.channelEventListener != null) {
                TransportClient.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
            }
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY CLIENT PIPELINE: CLOSE {}", remoteAddress);
            closeChannel(ctx.channel());
            super.close(ctx, future);

            if (TransportClient.this.channelEventListener != null) {
                TransportClient.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
                    log.warn("NETTY CLIENT PIPELINE: IDLE exception [{}]", remoteAddress);
                    closeChannel(ctx.channel());
                    if (TransportClient.this.channelEventListener != null) {
                        TransportClient.this.nettyEventExecutor.triggerNettyEvent(
                            new NettyEvent(NettyEventType.IDLE, remoteAddress, ctx.channel()));
                    }
                }
            }

            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.warn("NETTY CLIENT PIPELINE: exceptionCaught {}", remoteAddress);
            log.warn("NETTY CLIENT PIPELINE: exceptionCaught exception.", cause);
            closeChannel(ctx.channel());
            if (TransportClient.this.channelEventListener != null) {
                TransportClient.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.EXCEPTION, remoteAddress, ctx.channel()));
            }
        }
    }

}
