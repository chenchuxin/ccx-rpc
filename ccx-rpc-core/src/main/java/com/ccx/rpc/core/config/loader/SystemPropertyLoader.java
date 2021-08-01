package com.ccx.rpc.core.config.loader;

import cn.hutool.core.util.StrUtil;
import com.ccx.rpc.core.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * java 参数配置 -Dprefix.configField=xxx
 *
 * @author chenchuxin
 * @date 2021/8/1
 */
@Slf4j
public class SystemPropertyLoader implements ConfigLoader {

    @Override
    public <T> T loadConfig(Class<T> clazz) {
        Config configAnnotation = clazz.getAnnotation(Config.class);
        if (configAnnotation == null) {
            throw new IllegalStateException("config class " + clazz.getName() + " must has @Config annotation");
        }
        String prefix = configAnnotation.prefix();
        if (StrUtil.isBlank(prefix)) {
            throw new IllegalArgumentException("config class " + clazz.getName() + "@Config annotation must has prefix");
        }
        try {
            T configObject = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                String property = System.getProperty(prefix + "." + field.getName());
                Object typeValue = field.getType().cast(property);
                field.setAccessible(true);
                field.set(configObject, typeValue);
            }
            return configObject;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
