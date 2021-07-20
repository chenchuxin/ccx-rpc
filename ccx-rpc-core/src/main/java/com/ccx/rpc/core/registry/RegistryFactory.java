package com.ccx.rpc.core.registry;

import com.ccx.rpc.common.consts.URLKeyConst;
import com.ccx.rpc.common.extension.Adaptive;
import com.ccx.rpc.common.extension.SPI;
import com.ccx.rpc.common.url.URL;

/**
 * 注册中心工厂
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
@SPI
public interface RegistryFactory {

    @Adaptive(URLKeyConst.PROTOCOL)
    Registry getRegistry(URL url);
}
