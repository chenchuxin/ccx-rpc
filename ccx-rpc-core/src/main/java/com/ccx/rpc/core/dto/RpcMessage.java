package com.ccx.rpc.core.dto;

import com.ccx.rpc.core.consts.CompressType;
import com.ccx.rpc.core.consts.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenchuxin
 * @date 2021/7/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcMessage {
    /**
     * 消息类型 {@link MessageType#getValue()}
     */
    private byte messageType;

    /**
     * 压缩类型 {@link CompressType#getValue()}
     */
    private byte compressTye;

    /**
     * 序列化类型
     */
    private byte serializeType;

    private long requestId;

    private Object data;
}
