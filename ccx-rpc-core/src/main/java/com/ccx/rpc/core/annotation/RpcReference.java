package com.ccx.rpc.core.annotation;

import java.lang.annotation.*;

/**
 * Rpc 引用，服务调用方用
 *
 * @author chenchuxin
 * @date 2021/7/31
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    /**
     * 版本，没有特殊要求不用填写
     *
     * @return 版本，默认""
     */
    String version() default "";

}
