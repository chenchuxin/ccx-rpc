package com.ccx.rpc.core.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化类型
 *
 * @author chenchuxin
 * @date 2021/7/25
 */
@Getter
@AllArgsConstructor
public enum SerializeType {
    PROTOSTUFF((byte) 1, "protostuff");

    private final byte value;
    private final String name;

    public static SerializeType fromValue(byte value) {
        for (SerializeType serializeType : SerializeType.values()) {
            if (serializeType.getValue() == value) {
                return serializeType;
            }
        }
        return null;
    }

    public static SerializeType fromName(String name) {
        for (SerializeType serializeType : SerializeType.values()) {
            if (serializeType.getName().equals(name)) {
                return serializeType;
            }
        }
        return null;
    }
}
