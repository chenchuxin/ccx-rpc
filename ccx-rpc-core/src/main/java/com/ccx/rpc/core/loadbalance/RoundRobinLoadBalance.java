package com.ccx.rpc.core.loadbalance;

import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.core.dto.RpcRequest;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * 轮询
 *
 * @author chenchuxin
 * @date 2021/8/7
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    private final LongAdder curIndex = new LongAdder();

    @Override
    protected URL doSelect(List<URL> candidateUrls, RpcRequest request) {
        int index = (int) (curIndex.longValue() % candidateUrls.size());
        curIndex.increment();
        return candidateUrls.get(index);
    }
}
