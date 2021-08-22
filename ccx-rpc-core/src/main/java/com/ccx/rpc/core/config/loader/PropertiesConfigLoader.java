package com.ccx.rpc.core.config.loader;

import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * ccx-rpc.properties 文件配置。prefix.configField=xxx
 *
 * @author chenchuxin
 * @date 2021/8/22
 */
@Slf4j
public class PropertiesConfigLoader implements ConfigLoader {

    private Setting setting = null;

    public PropertiesConfigLoader() {
        try {
            setting = SettingUtil.get("ccx-rpc.properties");
        } catch (NoResourceException ex) {
            log.info("Config file 'ccx-rpc.properties' not exist!");
        }
    }


    @Override
    public String loadConfigItem(String key) {
        if (setting == null) {
            return null;
        }
        return setting.getStr(key);
    }
}
