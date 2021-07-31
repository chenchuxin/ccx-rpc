package com.ccx.rpc.core.remoting.dto;

import lombok.Data;

/**
 * RPC 请求实体
 *
 * @author chenchuxin
 * @date 2021/7/25
 */
@Data
public class RpcRequest {
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramTypes;
    private String version;
}
