package com.ccx.rpc.core.config;

import com.ccx.rpc.core.annotation.Config;
import lombok.Data;

/**
 * 服务启动配置
 *
 * @author chenchuxin
 * @date 2021/7/31
 */
@Data
@Config(prefix = "service")
public class ServiceConfig {
    /**
     * 监听的端口
     */
    private int port;
}
