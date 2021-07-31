package com.ccx.rpc.core.config;

import lombok.Data;

/**
 * 服务启动配置
 *
 * @author chenchuxin
 * @date 2021/7/31
 */
@Data
public class ServiceConfig {
    /**
     * 监听的端口
     */
    private int port;
}
