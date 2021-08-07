package com.ccx.rpc.core.config;

import com.ccx.rpc.core.annotation.Config;
import lombok.Data;

/**
 * 集群配置
 *
 * @author chenchuxin
 * @date 2021/8/7
 */
@Data
@Config(prefix = "cluster")
public class ClusterConfig {
    /**
     * 负载均衡策略
     */
    private String loadBalance;
}
