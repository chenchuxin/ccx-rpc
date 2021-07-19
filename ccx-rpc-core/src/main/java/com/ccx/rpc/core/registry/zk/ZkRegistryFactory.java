package com.ccx.rpc.core.registry.zk;

import com.ccx.rpc.core.registry.Registry;
import com.ccx.rpc.core.registry.RegistryFactory;
import com.ccx.rpc.common.url.URL;

/**
 * @author chenchuxin
 * @date 2021/7/18
 */
public class ZkRegistryFactory implements RegistryFactory {

    @Override
    public Registry getRegistry(URL url) {
        return new ZkRegistry(url);
    }

}
