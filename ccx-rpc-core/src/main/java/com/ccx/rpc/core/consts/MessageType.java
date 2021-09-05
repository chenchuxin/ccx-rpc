package com.ccx.rpc.core.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型
 *
 * @author chenchuxin
 * @date 2021/7/24
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    /**
     * 普通请求
     */
    REQUEST((byte) 1),

    /**
     * 普通响应
     */
    RESPONSE((byte) 2),

    /**
     * 心跳
     */
    HEARTBEAT((byte) 3),
    ;
    private final byte value;
}
