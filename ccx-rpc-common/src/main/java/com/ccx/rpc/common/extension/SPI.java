package com.ccx.rpc.common.extension;

import com.ccx.rpc.common.consts.URLKeyConst;

import java.lang.annotation.*;

/**
 * 被此注解标记的类，表示是一个扩展接口
 *
 * @author chenchuxin
 * @date 2021/7/12
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SPI {
    /**
     * 默认扩展类全路径
     *
     * @return 默认不填是 default
     */
    String value() default URLKeyConst.DEFAULT;
}
