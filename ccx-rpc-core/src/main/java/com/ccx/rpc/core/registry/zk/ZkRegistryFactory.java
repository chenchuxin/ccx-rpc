package com.ccx.rpc.core.registry.zk;

import com.ccx.rpc.core.registry.Registry;
import com.ccx.rpc.core.registry.RegistryFactory;
import com.ccx.rpc.common.url.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zk 注册中心工厂
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
public class ZkRegistryFactory implements RegistryFactory {

    private static final Map<URL, ZkRegistry> cache = new ConcurrentHashMap<>();

    @Override
    public Registry getRegistry(URL url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }
        ZkRegistry zkRegistry = new ZkRegistry(url);
        cache.putIfAbsent(url, zkRegistry);
        return cache.get(url);
    }

}
