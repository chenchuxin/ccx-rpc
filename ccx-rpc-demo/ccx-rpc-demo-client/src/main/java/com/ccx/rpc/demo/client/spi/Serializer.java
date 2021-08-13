package com.ccx.rpc.demo.client.spi;

import com.ccx.rpc.common.extension.SPI;

/**
 * @author chenchuxin
 * @date 2021/8/10
 */
@SPI
public interface Serializer {
    byte[] serialize(Object object);
}
