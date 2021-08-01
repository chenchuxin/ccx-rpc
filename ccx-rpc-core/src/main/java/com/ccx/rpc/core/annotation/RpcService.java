package com.ccx.rpc.core.annotation;

import java.lang.annotation.*;

/**
 * RPC 服务注解，打上这个注解的，将作为服务注册到注册中心
 *
 * @author chenchuxin
 * @date 2021/7/30
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    /**
     * 版本，如果同样签名的接口参数不兼容，可以用版本区分
     *
     * @return 默认空
     */
    String version() default "";
}
