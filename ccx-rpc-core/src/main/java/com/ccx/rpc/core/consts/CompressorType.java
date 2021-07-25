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
public enum CompressorType {
    GZIP((byte) 1, "gzip");

    private final byte value;
    private final String name;

    public static CompressorType fromValue(byte value) {
        for (CompressorType codecType : CompressorType.values()) {
            if (codecType.getValue() == value) {
                return codecType;
            }
        }
        return null;
    }
}
