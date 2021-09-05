package com.ccx.rpc.core.remoting.client.netty;

import com.ccx.rpc.core.consts.SerializeType;
import com.ccx.rpc.core.consts.CompressType;
import com.ccx.rpc.core.consts.MessageFormatConst;
import com.ccx.rpc.core.consts.MessageType;
import com.ccx.rpc.core.dto.RpcMessage;
import com.ccx.rpc.core.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenchuxin
 * @date 2021/7/31
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext context, RpcMessage requestMsg) {
        try {
            log.info("client receive msg: [{}]", requestMsg);
            if (requestMsg.getMessageType() == MessageType.RESPONSE.getValue()) {
                RpcResponse<?> response = (RpcResponse<?>) requestMsg.getData();
                UnprocessedRequests.complete(response);
            }
        } finally {
            ReferenceCountUtil.release(requestMsg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 心跳
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = ctx.channel();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setSerializeType(SerializeType.PROTOSTUFF.getValue());
                rpcMessage.setCompressTye(CompressType.DUMMY.getValue());
                rpcMessage.setMessageType(MessageType.HEARTBEAT.getValue());
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * Called when an exception occurs in processing a client message
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }

}
