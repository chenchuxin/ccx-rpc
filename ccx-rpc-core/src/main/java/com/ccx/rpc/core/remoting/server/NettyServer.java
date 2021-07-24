package com.ccx.rpc.core.remoting.server;

import lombok.Getter;

/**
 * @author chenchuxin
 * @date 2021/7/24
 */
public class NettyServer {

    @Getter
    private static final int PORT = 5630;

    public void start() {
        ShutdownHook.addShutdownHook();
    }
}
