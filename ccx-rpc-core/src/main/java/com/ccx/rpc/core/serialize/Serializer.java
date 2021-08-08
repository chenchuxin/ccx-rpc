package com.ccx.rpc.core.serialize;

import com.ccx.rpc.common.extension.SPI;

/**
 * 序列化器
 *
 * @author chenchuxin
 * @date 2021/7/20
 */
@SPI("protostuff")
public interface Serializer {

    /**
     * 序列化
     *
     * @param object 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     *
     * @param bytes 字节数组
     * @param clazz 要反序列化的类
     * @param <T>   类型
     * @return 反序列化的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
