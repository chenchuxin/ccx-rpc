package com.ccx.rpc.core.annotation;

import java.lang.annotation.*;

/**
 * 放在配置类上，可以使用 ConfigLoader 加载
 *
 * @author chenchuxin
 * @date 2021/8/1
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    /**
     * 前缀名
     *
     * @return 前缀，不能空
     */
    String prefix();
}
