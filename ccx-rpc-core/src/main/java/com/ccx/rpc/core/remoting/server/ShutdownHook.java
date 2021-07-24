package com.ccx.rpc.core.remoting.server;

import lombok.extern.slf4j.Slf4j;

/**
 * 关闭的钩子
 *
 * @author chenchuxin
 * @date 2021/7/24
 */
@Slf4j
public class ShutdownHook {

    public static void addShutdownHook() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // TODO: 注册中心全部解散
        }));
    }
}
