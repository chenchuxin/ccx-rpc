package com.ccx.rpc.common.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 网络工具
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
public class NetUtils {

    /**
     * 获取可用的端口
     *
     * @return 如果获取失败则返回随机的端口
     */
    public static int getAvailablePort() {
        try (ServerSocket ss = new ServerSocket()) {
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        }
    }

    /**
     * 获取随机端口
     *
     * @return 30000 ~ 39999
     */
    public static int getRandomPort() {
        return 30000 + ThreadLocalRandom.current().nextInt(10000);
    }
}
