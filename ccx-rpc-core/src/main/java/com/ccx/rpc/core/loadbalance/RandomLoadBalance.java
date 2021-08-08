package com.ccx.rpc.core.loadbalance;

import cn.hutool.core.util.RandomUtil;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.core.dto.RpcRequest;

import java.util.List;

/**
 * 随机负载均衡
 *
 * @author chenchuxin
 * @date 2021/8/7
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected URL doSelect(List<URL> candidateUrls, RpcRequest request) {
        int size = candidateUrls.size();
        int index = RandomUtil.randomInt(size);
        return candidateUrls.get(index);
    }
}
