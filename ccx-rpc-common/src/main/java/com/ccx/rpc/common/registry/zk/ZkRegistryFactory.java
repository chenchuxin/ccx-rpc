package com.ccx.rpc.common.registry.zk;

import com.ccx.rpc.common.registry.Registry;
import com.ccx.rpc.common.registry.RegistryFactory;
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
