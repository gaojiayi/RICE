package com.gaojy.rice.remote.transport;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.remote.ChannelEventListener;
import com.gaojy.rice.remote.IBaseRemote;
import com.gaojy.rice.remote.InvokeCallback;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.common.TransfUtil;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import java.net.InetSocketAddress;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TransportServer.java
 * @Description 服务端
 * @createTime 2022/01/02 11:47:00
 */
public class TransportServer extends AbstractRemoteService implements IBaseRemote {

    private static final Logger log = LoggerFactory.getLogger(RemoteHelper.RICE_REMOTING);
    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup eventLoopGroupSelector;
    private final EventLoopGroup eventLoopGroupBoss;
    private final TranfServerConfig tranfServerConfig;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private final ChannelEventListener channelEventListener;

    // TODO 使用配置端口
    private int port = 0;

    public TransportServer(TranfServerConfig tranfServerConfig) {
        this(tranfServerConfig, null);
    }

    public TransportServer(TranfServerConfig tranfServerConfig,
        ChannelEventListener channelEventListener) {
        super(tranfServerConfig.getServerOnewaySemaphoreValue(), tranfServerConfig.getServerAsyncSemaphoreValue());
        this.serverBootstrap = new ServerBootstrap();
        this.tranfServerConfig = tranfServerConfig;
        this.channelEventListener = channelEventListener;

        int publicThreadNums = tranfServerConfig.getServerCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }

        setPublicExecutor(Executors.newFixedThreadPool(publicThreadNums,
            new RiceThreadFactory("NettyServerPublicExecutor_")));
        this.eventLoopGroupBoss = new NioEventLoopGroup(1, new RiceThreadFactory("NettyBoss_"));

        if (useEpoll()) {
            this.eventLoopGroupSelector = new EpollEventLoopGroup(tranfServerConfig.getServerSelectorThreads(),
                new RiceThreadFactory("NettyServerEPOLLSelector_"));
        } else {
            this.eventLoopGroupSelector = new NioEventLoopGroup(tranfServerConfig.getServerSelectorThreads(),
                new RiceThreadFactory("NettyServerNIOSelector_"));
        }
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

    @Override public ChannelEventListener getChannelEventListener() {
        return channelEventListener;
    }

    @Override
    public void registerProcessor(int requestCode, RiceRequestProcessor processor,
        ExecutorService executor) {

    }

    @Override
    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(tranfServerConfig.getServerWorkerThreads(),
            new RiceThreadFactory("TransportServerCoreThread_"));

        ServerBootstrap childHandler =
            this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, tranfServerConfig.getServerSocketSndBufSize())
                .childOption(ChannelOption.SO_RCVBUF, tranfServerConfig.getServerSocketRcvBufSize())
                .localAddress(new InetSocketAddress(this.tranfServerConfig.getListenPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                            defaultEventExecutorGroup,
                            new NettyEncoder(TransportServer.this),
                            new NettyDecoder(TransportServer.this),
                            new IdleStateHandler(0, 0, tranfServerConfig.getServerChannelMaxIdleTimeSeconds()),
                            new NettyServerConnectManageHandler(),
                            new NettyChannelReadHandler());
                    }
                });

        if (tranfServerConfig.isServerPooledByteBufAllocatorEnable()) {
            childHandler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        try {
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            this.port = addr.getPort();
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        }

        if (this.channelEventListener != null) {
            this.nettyEventExecutor.start();
        }

        this.getTimer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    TransportServer.this.scanResponseTable();
                } catch (Throwable e) {
                    log.error("scanResponseTable exception", e);
                }
            }
        }, 1000 * 3, 1000);

    }

    /**
     * @description 判断是否可以使用Epoll模型
     */
    private boolean useEpoll() {
        return TransfUtil.isLinuxPlatform()
            && tranfServerConfig.isUseEpollNativeSelector()
            && Epoll.isAvailable();

    }

    @Override
    public void shutdown() {
        try {

            this.eventLoopGroupBoss.shutdownGracefully();

            this.eventLoopGroupSelector.shutdownGracefully();

            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
            super.shutdown();
        } catch (Exception e) {
            log.error("TransportServer shutdown exception, ", e);
        }
    }

    class NettyServerConnectManageHandler extends ChannelDuplexHandler {

        @Override public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY SERVER PIPELINE: channelRegistered {}", remoteAddress);
            super.channelRegistered(ctx);
        }

        @Override public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY SERVER PIPELINE: channelUnregistered, the channel[{}]", remoteAddress);
            super.channelUnregistered(ctx);
        }

        /**
         * TODO 由保存来自客户端的ChannelHandlerContext
         */
        @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY SERVER PIPELINE: channelActive, the channel[{}]", remoteAddress);
            super.channelActive(ctx);
            TransportServer.this.addChannel(remoteAddress, ctx.channel());
            if (TransportServer.this.channelEventListener != null) {
                TransportServer.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.CONNECT, remoteAddress, ctx.channel()));
            }
        }

        @Override public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY SERVER PIPELINE: channelInactive, the channel[{}]", remoteAddress);
            super.channelInactive(ctx);

            if (TransportServer.this.channelEventListener != null) {
                TransportServer.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
            }
        }

        @Override public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
                    log.warn("NETTY SERVER PIPELINE: IDLE exception [{}]", remoteAddress);
                    closeChannel(ctx.channel());
                    if (TransportServer.this.channelEventListener != null) {
                        TransportServer.this.nettyEventExecutor.triggerNettyEvent(
                            new NettyEvent(NettyEventType.IDLE, remoteAddress, ctx.channel()));
                    }
                }
            }

            ctx.fireUserEventTriggered(evt);
        }

        @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            log.warn("NETTY SERVER PIPELINE: exceptionCaught {}", remoteAddress);
            log.warn("NETTY SERVER PIPELINE: exceptionCaught exception.", cause);

            if (TransportServer.this.channelEventListener != null) {
                TransportServer.this.nettyEventExecutor.triggerNettyEvent(
                    new NettyEvent(NettyEventType.EXCEPTION, remoteAddress, ctx.channel()));
            }

            closeChannel(ctx.channel());
        }
    }
}
