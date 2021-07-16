package com.ccx.rpc.common.test.extension;

/**
 * 测试用的默认扩展
 *
 * @author chenchuxin
 * @date 2021/7/13
 */
public class DefaultExtension implements Extension {
    @Override
    public void doSomething() {
        System.out.println("DefaultExtensionTest doing...");
    }
}
