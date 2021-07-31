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

    /**
     * 获取注册中心
     *
     * @param url 注册中心的配置，例如注册中心的地址。会自动根据协议获取注册中心实例
     * @return 如果协议类型跟注册中心匹配上了，返回对应的配置中心实例
     */
    @Adaptive(URLKeyConst.PROTOCOL)
    Registry getRegistry(URL url);
}
