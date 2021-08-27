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
    /**
     * 伪压缩器，啥事不干。有一些序列化工具压缩已经做得很好了，无需再压缩
     */
    DUMMY((byte) 0, "dummy"),

    GZIP((byte) 1, "gzip");

    private final byte value;
    private final String name;

    /**
     * 通过值获取压缩类型枚举
     *
     * @param value 值
     * @return 如果获取不到，返回 DUMMY
     */
    public static CompressType fromValue(byte value) {
        for (CompressType codecType : CompressType.values()) {
            if (codecType.getValue() == value) {
                return codecType;
            }
        }
        return DUMMY;
    }

    /**
     * 通过名字获取压缩类型枚举
     *
     * @param name 名字
     * @return 如果获取不到，返回 DUMMY
     */
    public static CompressType fromName(String name) {
        for (CompressType codecType : CompressType.values()) {
            if (codecType.getName().equals(name)) {
                return codecType;
            }
        }
        return DUMMY;
    }
}
