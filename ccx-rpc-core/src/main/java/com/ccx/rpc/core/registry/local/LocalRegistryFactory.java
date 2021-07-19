package com.ccx.rpc.core.registry.local;

import com.ccx.rpc.core.registry.Registry;
import com.ccx.rpc.core.registry.RegistryFactory;
import com.ccx.rpc.common.url.URL;

/**
 * @author chenchuxin
 * @date 2021/7/18
 */
public class LocalRegistryFactory implements RegistryFactory {

    @Override
    public Registry getRegistry(URL url) {
        return new LocalRegistry();
    }
}
