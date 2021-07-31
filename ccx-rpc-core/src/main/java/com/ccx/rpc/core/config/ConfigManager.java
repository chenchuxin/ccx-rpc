package com.ccx.rpc.core.config;

import javax.xml.ws.Service;

/**
 * 配置管理
 *
 * @author chenchuxin
 * @date 2021/7/31
 */
public class ConfigManager {

    private ConfigManager() {
    }

    private static final ConfigManager instant = new ConfigManager();

    public static ConfigManager getInstant() {
        return instant;
    }

    /**
     * 获取注册中心的配置
     */
    public RegistryConfig getRegistryConfig() {
        // TODO: 配置如何 load
        return new RegistryConfig();
    }

    public ServiceConfig getServiceConfig() {
        return new ServiceConfig();
    }

    public ProtocolConfig getProtocolConfig() {
        return new ProtocolConfig();
    }
}
