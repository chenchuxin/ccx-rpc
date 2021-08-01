package com.ccx.rpc.core.remoting.server.netty;

import com.ccx.rpc.common.consts.RpcException;
import com.ccx.rpc.core.consts.MessageFormatConst;
import com.ccx.rpc.core.consts.MessageType;
import com.ccx.rpc.core.proxy.RpcServiceCache;
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

import java.lang.reflect.Method;

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
                    .serializeType(requestMsg.getSerializeType())
                    .compressTye(requestMsg.getCompressTye())
                    .requestId(requestMsg.getRequestId());
            if (requestMsg.getMessageType() == MessageType.HEARTBEAT_PING.getValue()) {
                responseMsgBuilder.messageType(MessageType.HEARTBEAT_PONG.getValue());
                responseMsgBuilder.data(MessageFormatConst.PONG_DATA);
            } else {
                RpcRequest rpcRequest = (RpcRequest) requestMsg.getData();
                Object result;
                try {
                    // 根据请求的接口名和版本，获取服务。这个服务是在bean初始化的时候加上的
                    Object service = RpcServiceCache.getService(rpcRequest.getRpcServiceForCache());
                    Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                    // 开始执行
                    result = method.invoke(service, rpcRequest.getParams());
                    log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
                } catch (Exception e) {
                    throw new RpcException(e.getMessage(), e);
                }
                responseMsgBuilder.messageType(MessageType.RESPONSE.getValue());
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
                    responseMsgBuilder.data(response);
                } else {
                    responseMsgBuilder.data(RpcResponse.fail());
                    log.error("not writable now, message dropped");
                }
            }
            ctx.writeAndFlush(responseMsgBuilder.build()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
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
