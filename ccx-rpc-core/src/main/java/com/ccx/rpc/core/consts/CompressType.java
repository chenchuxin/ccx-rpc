package com.ccx.rpc.core.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 压缩类型
 *
 * @author chenchuxin
 * @date 2021/7/24
 */
@Getter
@AllArgsConstructor
public enum CompressType {
    GZIP((byte) 1),
    ;
    private final byte value;
}
