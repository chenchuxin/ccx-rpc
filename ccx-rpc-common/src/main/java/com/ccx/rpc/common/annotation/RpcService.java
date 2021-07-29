package com.ccx.rpc.common.annotation;

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
}
