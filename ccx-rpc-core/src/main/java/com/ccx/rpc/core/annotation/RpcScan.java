package com.ccx.rpc.core.annotation;

import com.ccx.rpc.core.spring.RpcScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author chenchuxin
 * @date 2021/7/30
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcScannerRegistrar.class)
public @interface RpcScan {
    String[] basePackages();
}
