package com.ccx.rpc.core.remoting.dto;

import com.google.common.base.Joiner;
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
    private String group;

    /**
     * 获取服务名
     */
    public String getServiceName() {
        return Joiner.on("_").join(interfaceName, group, version);
    }
}
