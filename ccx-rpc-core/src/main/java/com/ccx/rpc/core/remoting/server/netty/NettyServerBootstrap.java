package com.ccx.rpc.core.remoting.server.netty;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.ccx.rpc.core.config.ConfigManager;
import com.ccx.rpc.core.config.ServiceConfig;
import com.ccx.rpc.core.remoting.codec.RpcMessageDecoder;
import com.ccx.rpc.core.remoting.codec.RpcMessageEncoder;
import com.ccx.rpc.core.remoting.server.ShutdownHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * netty 服务端
 *
 * @author chenchuxin
 * @date 2021/7/24
 */
@Slf4j
@Component
public class NettyServerBootstrap {

    public void start() {
        ShutdownHook.addShutdownHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.getProcessorCount() * 2,
                ThreadUtil.newNamedThreadFactory("service-handler-group", false)
        );
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    // 系统用于临时存放已完成三次握手的请求的队列的最大长度。如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 程序进程非正常退出，内核需要一定的时间才能够释放此端口，不设置 SO_REUSEADDR 就无法正常使用该端口。
                    .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                    // TCP/IP协议中针对TCP默认开启了Nagle 算法。
                    // Nagle 算法通过减少需要传输的数据包，来优化网络。在内核实现中，数据包的发送和接受会先做缓存，分别对应于写缓存和读缓存。
                    // 启动 TCP_NODELAY，就意味着禁用了 Nagle 算法，允许小包的发送。
                    // 对于延时敏感型，同时数据传输量比较小的应用，开启TCP_NODELAY选项无疑是一个正确的选择
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            // 编解码器
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            // RPC 消息处理器
                            p.addLast(serviceHandlerGroup, new NettyServerHandler());
                        }
                    });
            // 绑定端口，同步等待绑定成功
            ServiceConfig serviceConfig = ConfigManager.getInstant().getServiceConfig();
            ChannelFuture channelFuture = bootstrap.bind(NetUtil.getLocalHostName(), serviceConfig.getPort()).sync();
            log.info("server start success. port=" + serviceConfig.getPort());
            // 等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception ex) {
            log.error("shutdown bossGroup and workerGroup", ex);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
