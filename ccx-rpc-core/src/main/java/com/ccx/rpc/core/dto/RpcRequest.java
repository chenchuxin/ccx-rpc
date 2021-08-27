package com.ccx.rpc.core.dto;

import cn.hutool.core.util.StrUtil;
import lombok.*;

/**
 * RPC 请求实体
 *
 * @author chenchuxin
 * @date 2021/7/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramTypes;
    private String version;

    public String getRpcServiceForCache() {
        if (StrUtil.isBlank(version)) {
            return interfaceName;
        }
        return interfaceName + "_" + version;
    }
}
