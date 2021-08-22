package com.ccx.rpc.core.config.loader;

import lombok.extern.slf4j.Slf4j;

/**
 * java 参数配置 -Dprefix.configField=xxx
 *
 * @author chenchuxin
 * @date 2021/8/1
 */
@Slf4j
public class SystemPropertyLoader implements ConfigLoader {

    @Override
    public String loadConfigItem(String key) {
        return System.getProperty(key);
    }
}
