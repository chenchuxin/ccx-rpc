package com.ccx.rpc.core.remoting.client.netty;

import cn.hutool.core.util.StrUtil;
import com.ccx.rpc.common.consts.RpcException;
import com.ccx.rpc.common.consts.URLKeyConst;
import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.common.url.URLBuilder;
import com.ccx.rpc.core.config.ConfigManager;
import com.ccx.rpc.core.config.ProtocolConfig;
import com.ccx.rpc.core.consts.SerializeType;
import com.ccx.rpc.core.consts.CompressType;
import com.ccx.rpc.core.consts.MessageType;
import com.ccx.rpc.core.registry.Registry;
import com.ccx.rpc.core.registry.RegistryFactory;
import com.ccx.rpc.core.remoting.codec.RpcMessageDecoder;
import com.ccx.rpc.core.remoting.codec.RpcMessageEncoder;
import com.ccx.rpc.core.remoting.dto.RpcMessage;
import com.ccx.rpc.core.remoting.dto.RpcRequest;
import com.ccx.rpc.core.remoting.dto.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author chenchuxin
 * @date 2021/7/31
 */
@Slf4j
public class NettyClient {

    private final Bootstrap bootstrap;

    /**
     * {地址：连接的channel}
     */
    private static final Map<SocketAddress, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private final Registry registry;

    private static NettyClient instance = null;

    public static NettyClient getInstance() {
        if (instance == null) {
            synchronized (NettyClient.class) {
                if (instance == null) {
                    instance = new NettyClient();
                }
            }
        }
        return instance;
    }

    private NettyClient() {
        bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // 设定 IdleStateHandler 心跳检测每 5 秒进行一次写检测
                        // write()方法超过 5 秒没调用，就调用 userEventTrigger
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyClientHandler());
                    }
                });
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
        registry = registryFactory.getRegistry(ConfigManager.getInstant().getRegistryConfig().toURL());
    }

    /**
     * 发送 RPC 请求
     *
     * @param request 请求数据
     * @return 响应
     */
    public CompletableFuture<RpcResponse<?>> sendRpcRequest(RpcRequest request) {
        Map<String, String> serviceParam = URLBuilder.getServiceParam(request.getInterfaceName(), request.getVersion());
        URL url = URL.builder().protocol(URLKeyConst.CCX_RPC_PROTOCOL).host(URLKeyConst.ANY_HOST).params(serviceParam).build();
        List<URL> urls = registry.lookup(url);
        // TODO: 负载
        URL serverUrl = urls.get(0);
        InetSocketAddress socketAddress = new InetSocketAddress(serverUrl.getHost(), serverUrl.getPort());
        Channel channel = getChannel(socketAddress);
        if (channel.isActive()) {
            CompletableFuture<RpcResponse<?>> resultFuture = new CompletableFuture<>();
            UnprocessedRequests.put(request.getRequestId(), resultFuture);
            RpcMessage rpcMessage = buildRpcMessage(request);
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("send failed:", future.cause());
                }
            });
            return resultFuture;
        } else {
            throw new IllegalStateException("channel is not active. address=" + socketAddress);
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
        if (compressType == null) {
            throw new IllegalStateException("compressType " + compressTypeName + " not support.");
        }

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
                .data(request)
                .build();
    }

    /**
     * 获取和指定地址连接的 channel，如果获取不到，则连接
     *
     * @param address 指定要连接的地址
     * @return channel
     */
    public Channel getChannel(SocketAddress address) {
        Channel channel = CHANNEL_MAP.get(address);
        if (channel == null || !channel.isActive()) {
            channel = connect(address);
            CHANNEL_MAP.put(address, channel);
        }
        return channel;
    }

    /**
     * 连接地址
     *
     * @param address 地址
     * @return channel
     */
    private Channel connect(SocketAddress address) {
        try {
            CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            ChannelFuture connect = bootstrap.connect(address);
            connect.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    completableFuture.complete(future.channel());
                } else {
                    throw new IllegalStateException(StrUtil.format("connect fail. address", address));
                }
            });
            return completableFuture.get();
        } catch (Exception ex) {
            throw new RpcException(address + " connect fail.", ex);
        }
    }
}
