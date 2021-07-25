package com.ccx.rpc.core.remoting.dto;

import com.ccx.rpc.core.consts.CompressorType;
import com.ccx.rpc.core.consts.MessageType;
import lombok.Builder;
import lombok.Data;

/**
 * @author chenchuxin
 * @date 2021/7/24
 */
@Data
@Builder
public class RpcMessage {
    /**
     * 消息类型 {@link MessageType#getValue()}
     */
    private byte messageType;

    /**
     * 压缩类型 {@link CompressorType#getValue()}
     */
    private byte compress;

    /**
     * 序列化类型
     */
    private byte codec;

    private long requestId;

    private Object data;
}
