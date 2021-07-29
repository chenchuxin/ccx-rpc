package com.ccx.rpc.common.annotation;

import java.lang.annotation.*;

/**
 * @author chenchuxin
 * @date 2021/7/30
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcScan {
    String[] basePackages();
}
