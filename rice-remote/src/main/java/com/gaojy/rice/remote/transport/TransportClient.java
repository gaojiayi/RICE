package com.gaojy.rice.remote.transport;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.remote.ChannelEventListener;
import com.gaojy.rice.remote.IBaseRemote;
import com.gaojy.rice.remote.InvokeCallback;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelDuplexHandler;
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
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TransportClient.java
 * @Description TODO
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
    public RiceRemoteContext invokeSync(String addr, RiceRemoteContext request, long timeoutMillis) {
        return null;
    }

    @Override
    public void invokeAsync(String addr, RiceRemoteContext request, long timeoutMillis,
        InvokeCallback invokeCallback) {

    }

    @Override
    public void invokeOneWay(String addr, RiceRemoteContext request) {

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
                        new NettyChannelReadHandler());
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
            this.eventLoopGroupWorker.shutdownGracefully();

            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
            super.shutdown();

        } catch (Exception e) {
            log.error("TransportClient shutdown exception, ", e);
        }
    }

    @Override public ChannelEventListener getChannelEventListener() {
        return channelEventListener;
    }

    /**
     * @description 监听到netty事件之后，把对应的时间放到nettyEventExecutor中的队列中。由后台线程负责调用channelEventListener
     */
    class NettyClientConnectManageHandler extends ChannelDuplexHandler {

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

        @Override public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY CLIENT PIPELINE: DISCONNECT {}", remoteAddress);
            closeChannel(ctx.channel());
            super.disconnect(ctx, future);

            if (TransportClient.this.channelEventListener != null) {
                TransportClient.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
            }
        }

        @Override public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY CLIENT PIPELINE: CLOSE {}", remoteAddress);
            closeChannel(ctx.channel());
            super.close(ctx, future);

            if (TransportClient.this.channelEventListener != null) {
                TransportClient.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
            }
        }

        @Override public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
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

        @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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
