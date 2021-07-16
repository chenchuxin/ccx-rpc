package com.ccx.rpc.common.test.extension;

/**
 * 测试用的其他扩展
 *
 * @author chenchuxin
 * @date 2021/7/13
 */
public class OtherExtension implements Extension {
    @Override
    public void doSomething() {
        System.out.println("OtherExtensionTest doing...");
    }
}
