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
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数列表
     */
    private Object[] params;
    /**
     * 参数类型列表
     */
    private Class<?>[] paramTypes;
    /**
     * 接口版本
     */
    private String version;

    public String getRpcServiceForCache() {
        if (StrUtil.isBlank(version)) {
            return interfaceName;
        }
        return interfaceName + "_" + version;
    }
}
