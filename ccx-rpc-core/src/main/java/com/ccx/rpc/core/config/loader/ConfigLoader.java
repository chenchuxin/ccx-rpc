package com.ccx.rpc.core.config.loader;

import com.ccx.rpc.common.extension.SPI;

/**
 * 配置加载器
 *
 * @author chenchuxin
 * @date 2021/8/1
 */
@SPI
public interface ConfigLoader {

    <T> T loadConfig(Class<T> clazz);

}
