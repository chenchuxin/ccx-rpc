package com.ccx.rpc.common.test.extension;

import com.ccx.rpc.common.extension.SPI;

/**
 * 测试用的扩展接口
 *
 * @author chenchuxin
 * @date 2021/7/13
 */
@SPI("default")
public interface ExtensionNotFile {
    void doSomething();
}
