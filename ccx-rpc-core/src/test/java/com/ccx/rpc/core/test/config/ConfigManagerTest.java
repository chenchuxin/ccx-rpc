package com.ccx.rpc.core.test.config;

import com.ccx.rpc.core.config.ConfigManager;
import com.ccx.rpc.core.config.RegistryConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author chenchuxin
 * @date 2021/8/22
 */
public class ConfigManagerTest {

    private static final ConfigManager CONFIG_MANAGER = ConfigManager.getInstant();

    @Test
    public void systemPropertyLoaderTest() {
        String address = "zk://localhost:2181";
        System.setProperty("registry.address", address);
        RegistryConfig registryConfig = CONFIG_MANAGER.getRegistryConfig();
        Assert.assertEquals(address, registryConfig.getAddress());
    }

    @Test
    public void propertiesLoaderTest() {
        String address = "zk://localhost:2181";
        RegistryConfig registryConfig = CONFIG_MANAGER.getRegistryConfig();
        Assert.assertEquals(address, registryConfig.getAddress());
    }

    @Test
    public void loaderPriorityTest() {
        String systemPropertyAddress = "zk://localhost2:2181";
        System.setProperty("registry.address", systemPropertyAddress);
        RegistryConfig registryConfig = CONFIG_MANAGER.getRegistryConfig();
        Assert.assertEquals(systemPropertyAddress, registryConfig.getAddress());
    }
}
