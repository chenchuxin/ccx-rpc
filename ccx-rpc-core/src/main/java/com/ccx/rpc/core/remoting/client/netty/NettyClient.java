package com.ccx.rpc.core.remoting.client.netty;

import cn.hutool.core.util.StrUtil;
import com.ccx.rpc.common.consts.RpcException;
import com.ccx.rpc.core.remoting.codec.RpcMessageDecoder;
import com.ccx.rpc.core.remoting.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
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
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
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
            log.info("Try to connect server [{}]", address);
            CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            ChannelFuture connect = bootstrap.connect(address);
            connect.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    completableFuture.complete(future.channel());
                } else {
                    throw new IllegalStateException(StrUtil.format("connect fail. address:", address));
                }
            });
            return completableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new RpcException(address + " connect fail.", ex);
        }
    }
}
