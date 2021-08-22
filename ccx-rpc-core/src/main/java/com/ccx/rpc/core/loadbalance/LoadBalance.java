package com.ccx.rpc.core.loadbalance;

import com.ccx.rpc.common.extension.SPI;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.core.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡
 *
 * @author chenchuxin
 * @date 2021/8/7
 */
@SPI("round-robin")
public interface LoadBalance {

    /**
     * 选择
     *
     * @param candidateUrls 候选的 URL
     * @param request       请求
     * @return 选择的 URL
     */
    URL select(List<URL> candidateUrls, RpcRequest request);
}
