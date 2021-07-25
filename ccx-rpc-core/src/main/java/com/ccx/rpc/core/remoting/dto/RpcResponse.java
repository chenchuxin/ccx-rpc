package com.ccx.rpc.core.remoting.dto;

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
    private String requestId;
    private Integer code;
    private String message;
    private T data;
}
