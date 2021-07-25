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
public enum CodecType {
    PROTOSTUFF((byte) 1, "protostuff");

    private final byte value;
    private final String name;

    public static CodecType fromValue(byte value) {
        for (CodecType codecType : CodecType.values()) {
            if (codecType.getValue() == value) {
                return codecType;
            }
        }
        return null;
    }
}
