package com.ccx.rpc.demo.client.spi;

/**
 * @author chenchuxin
 * @date 2021/8/10
 */
public interface Serializer {
    byte[] serialize(Object object);
}
