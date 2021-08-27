package com.ccx.rpc.core.invoke;

import com.ccx.rpc.common.consts.RpcException;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.core.config.ConfigManager;
import com.ccx.rpc.core.config.ProtocolConfig;
import com.ccx.rpc.core.consts.CompressType;
import com.ccx.rpc.core.consts.MessageFormatConst;
import com.ccx.rpc.core.consts.MessageType;
import com.ccx.rpc.core.consts.SerializeType;
import com.ccx.rpc.core.dto.*;
import com.ccx.rpc.core.remoting.client.netty.NettyClient;
import com.ccx.rpc.core.remoting.client.netty.UnprocessedRequests;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * netty 执行者，相当于发请求
 *
 * @author chenchuxin
 * @date 2021/8/8
 */
@Slf4j
public class NettyInvoker extends AbstractInvoker {

    private final NettyClient nettyClient = NettyClient.getInstance();

    @Override
    protected RpcResult doInvoke(RpcRequest request, URL selected) throws RpcException {
        InetSocketAddress socketAddress = new InetSocketAddress(selected.getHost(), selected.getPort());
        Channel channel = nettyClient.getChannel(socketAddress);
        if (channel.isActive()) {
            CompletableFuture<RpcResponse<?>> resultFuture = new CompletableFuture<>();
            // 构建 RPC 消息，此处会构建 requestId
            RpcMessage rpcMessage = buildRpcMessage(request);
            UnprocessedRequests.put(rpcMessage.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("send failed:", future.cause());
                }
            });
            return new AsyncResult(resultFuture);
        } else {
            throw new RpcException("channel is not active. address=" + socketAddress);
        }
    }

    /**
     * 根据请求数据，构建 Rpc 通用信息结构
     *
     * @param request 请求
     * @return RpcMessage
     */
    private RpcMessage buildRpcMessage(RpcRequest request) {
        ProtocolConfig protocolConfig = ConfigManager.getInstant().getProtocolConfig();

        // 压缩类型
        String compressTypeName = protocolConfig.getCompressType();
        CompressType compressType = CompressType.fromName(compressTypeName);

        // 序列化类型
        String serializeTypeName = protocolConfig.getSerializeType();
        SerializeType serializeType = SerializeType.fromName(serializeTypeName);
        if (serializeType == null) {
            throw new IllegalStateException("serializeType " + serializeTypeName + " not support.");
        }

        return RpcMessage.builder()
                .messageType(MessageType.REQUEST.getValue())
                .compressTye(compressType.getValue())
                .serializeType(serializeType.getValue())
                .requestId(MessageFormatConst.REQUEST_ID.getAndIncrement())
                .data(request)
                .build();
    }
}
