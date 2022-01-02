package com.gaojy.rice.remote.transport;

import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName NettyEncoder.java
 * @Description Netty 编码器
 * @createTime 2022/01/02 11:03:00
 */
public class NettyEncoder extends MessageToByteEncoder<RiceRemoteContext> {
    private static final Logger log = LoggerFactory.getLogger(RemoteHelper.RICE_REMOTING);
    private AbstractRemoteService ars;

    public NettyEncoder(AbstractRemoteService ars) {
        super();
        this.ars = ars;
    }

    @Override
    protected void encode(ChannelHandlerContext context, RiceRemoteContext riceRemoteContext,
        ByteBuf out) throws Exception {
        try {
            ByteBuffer header = riceRemoteContext.encodeHeader();
            out.writeBytes(header);
            byte[] body = riceRemoteContext.getBody();
            if (body != null) {
                out.writeBytes(body);
            }
        } catch (Exception e) {
            log.error("encode exception, " + RemoteHelper.parseChannelRemoteAddr(context.channel()), e);
            if (riceRemoteContext != null) {
                log.error(riceRemoteContext.toString());
            }
            ars.closeChannel(context.channel());
        }
    }
}
