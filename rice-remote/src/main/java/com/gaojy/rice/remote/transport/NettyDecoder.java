package com.gaojy.rice.remote.transport;

import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.common.TransfUtil;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName NettyDecoder.java
 * @Description NETTY 解码器
 * @createTime 2022/01/02 11:10:00
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger log = LoggerFactory.getLogger(RemoteHelper.RICE_REMOTING);
    private AbstractRemoteService ars;

    private static final int FRAME_MAX_LENGTH = //
        Integer.parseInt(System.getProperty("com.rice.remoting.frameMaxLength", "16777216"));

    public NettyDecoder(AbstractRemoteService ars) {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
        this.ars = ars;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }

            ByteBuffer byteBuffer = frame.nioBuffer();

            return RiceRemoteContext.decode(byteBuffer);
        } catch (Exception e) {
            log.error("decode exception, " + RemoteHelper.parseChannelRemoteAddr(ctx.channel()), e);
            TransfUtil.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                frame.release();
            }
        }

        return null;
    }
}
