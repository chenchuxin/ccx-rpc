package com.ccx.rpc.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenchuxin
 * @date 2021/7/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {
    /**
     * 请求id
     */
    private long requestId;
    /**
     * 响应码
     */
    private Integer code;
    /**
     * 提示消息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data, long requestId) {
        return RpcResponse.<T>builder()
                .code(RpcResponseCode.SUCCESS.getCode())
                .message(RpcResponseCode.SUCCESS.getMessage())
                .data(data).requestId(requestId)
                .build();
    }

    public static <T> RpcResponse<T> fail() {
        return RpcResponse.<T>builder()
                .code(RpcResponseCode.FAIL.getCode())
                .message(RpcResponseCode.FAIL.getMessage())
                .build();
    }

    public static <T> RpcResponse<T> fail(RpcResponseCode code) {
        return RpcResponse.<T>builder()
                .code(code.getCode())
                .message(code.getMessage())
                .build();
    }
}
