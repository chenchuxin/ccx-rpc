package com.ccx.rpc.core.config;

import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.core.config.loader.ConfigLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置管理
 *
 * @author chenchuxin
 * @date 2021/7/31
 */
public class ConfigManager {

    private ConfigLoader configLoader;

    private Map<Class, Object> configCache = new ConcurrentHashMap<>();

    private ConfigManager() {
        configLoader = ExtensionLoader.getExtensionLoader(ConfigLoader.class).getExtension("system-property");
    }

    private static final ConfigManager instant = new ConfigManager();

    public static ConfigManager getInstant() {
        return instant;
    }


    /**
     * 加载配置，有缓存
     *
     * @param clazz 配置类型
     * @param <T>
     * @return 配置实体类
     */
    @SuppressWarnings("unchecked")
    public <T> T loadConfig(Class<T> clazz) {
        T config = (T) configCache.get(clazz);
        if (config == null) {
            config = configLoader.loadConfig(clazz);
            configCache.put(clazz, config);
        }
        return config;
    }

    /**
     * 获取注册中心的配置
     */
    public RegistryConfig getRegistryConfig() {
        return loadConfig(RegistryConfig.class);
    }

    public ServiceConfig getServiceConfig() {
        return loadConfig(ServiceConfig.class);
    }

    public ProtocolConfig getProtocolConfig() {
        return loadConfig(ProtocolConfig.class);
    }

    public ClusterConfig getClusterConfig() {
        return loadConfig(ClusterConfig.class);
    }
}
