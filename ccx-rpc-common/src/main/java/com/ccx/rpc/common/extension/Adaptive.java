package com.ccx.rpc.common.extension;

import java.lang.annotation.*;

/**
 * 与 {@link SPI} 联合使用，在方法上面标记。<br>
 * 使用的方法中必须要有 URL 参数 <br>
 * 代理生成的扩展类会自动读取 URL 参数上的 {@link Adaptive#value()} 参数，再根据这个参数类型，获取对应的扩展类
 *
 * @author chenchuxin
 * @date 2021/7/19
 * @see SPI
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Adaptive {
    String value() default "";
}
