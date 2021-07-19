package com.ccx.rpc.core.registry;

import com.ccx.rpc.common.extension.SPI;
import com.ccx.rpc.common.url.URL;

/**
 * 注册中心工厂
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
@SPI("zk")
public interface RegistryFactory {

    Registry getRegistry(URL url);
}
