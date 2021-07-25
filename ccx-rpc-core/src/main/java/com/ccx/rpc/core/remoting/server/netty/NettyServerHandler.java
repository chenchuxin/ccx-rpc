package com.ccx.rpc.core.remoting.server.netty;

import com.ccx.rpc.core.consts.MessageFormatConst;
import com.ccx.rpc.core.consts.MessageType;
import com.ccx.rpc.core.remoting.dto.RpcMessage;
import com.ccx.rpc.core.remoting.dto.RpcRequest;
import com.ccx.rpc.core.remoting.dto.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenchuxin
 * @date 2021/7/25
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage requestMsg) {
        try {
            RpcMessage.RpcMessageBuilder responseMsgBuilder = RpcMessage.builder()
                    .codec(requestMsg.getCodec())
                    .compress(requestMsg.getCompress());
            if (requestMsg.getMessageType() == MessageType.HEARTBEAT_PING.getValue()) {
                responseMsgBuilder.messageType(MessageType.HEARTBEAT_PONG.getValue());
                responseMsgBuilder.data(MessageFormatConst.PONG_DATA);
            } else {
                RpcRequest requestMsgData = (RpcRequest) requestMsg.getData();
                // TODO：调用服务代理进行处理
                Object result = null;
                responseMsgBuilder.messageType(MessageType.RESPONSE.getValue());
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    RpcResponse<Object> response = RpcResponse.success(result, requestMsgData.getRequestId());
                    responseMsgBuilder.data(response);
                } else {
                    responseMsgBuilder.data(RpcResponse.fail());
                    log.error("not writable now, message dropped");
                }
                ctx.writeAndFlush(responseMsgBuilder.build()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(requestMsg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 处理空闲状态的
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception", cause);
        ctx.close();
    }
}
