package com.ccx.rpc.core.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 压缩类型
 *
 * @author chenchuxin
 * @date 2021/7/25
 */
@Getter
@AllArgsConstructor
public enum CompressType {
    GZIP((byte) 1, "gzip");

    private final byte value;
    private final String name;

    public static CompressType fromValue(byte value) {
        for (CompressType codecType : CompressType.values()) {
            if (codecType.getValue() == value) {
                return codecType;
            }
        }
        return null;
    }

    public static CompressType fromName(String name) {
        for (CompressType codecType : CompressType.values()) {
            if (codecType.getName().equals(name)) {
                return codecType;
            }
        }
        return null;
    }
}
