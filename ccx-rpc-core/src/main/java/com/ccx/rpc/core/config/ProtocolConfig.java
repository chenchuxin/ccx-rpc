package com.ccx.rpc.core.config;

import com.ccx.rpc.core.consts.CompressType;
import com.ccx.rpc.core.consts.SerializeType;
import lombok.Data;

/**
 * 协议相关配置
 *
 * @author chenchuxin
 * @date 2021/8/1
 */
@Data
public class ProtocolConfig {

    /**
     * 序列化类型 {@link SerializeType}
     */
    private String serializeType;

    /**
     * 压缩类型 {@link CompressType}
     */
    private String compressType;
}
