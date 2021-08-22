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

    /**
     * 加载配置项
     *
     * @param key 配置的 key
     * @return 配置项的值，如果不存在，返回 null
     */
    String loadConfigItem(String key);

}
