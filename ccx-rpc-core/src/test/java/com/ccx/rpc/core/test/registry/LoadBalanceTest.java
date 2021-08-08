package com.ccx.rpc.core.test.registry;

import cn.hutool.core.collection.ListUtil;
import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.core.loadbalance.LoadBalance;
import com.ccx.rpc.core.dto.RpcRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负载均衡测试
 *
 * @author chenchuxin
 * @date 2021/8/7
 */
public class LoadBalanceTest {
    private static final LoadBalance RANDOM_LOAD_BALANCE = ExtensionLoader.getLoader(LoadBalance.class).getExtension("random");
    private static final LoadBalance ROUND_ROBIN_LOAD_BALANCE = ExtensionLoader.getLoader(LoadBalance.class).getExtension("round-robin");
    private static final List<URL> candidateUrls = ListUtil.toList(
            URL.valueOf("zk://127.0.0.1:1000"),
            URL.valueOf("zk://127.0.0.1:2000"),
            URL.valueOf("zk://127.0.0.1:3000")
    );
    private static final RpcRequest RPC_REQUEST = RpcRequest.builder().build();

    @Test
    public void randomTest() {
        Map<URL, Integer> counter = new HashMap<>(4);
        int runs = 50000;
        for (int i = 0; i < runs; i++) {
            URL url = RANDOM_LOAD_BALANCE.select(candidateUrls, RPC_REQUEST);
            counter.put(url, counter.getOrDefault(url, 0) + 1);
        }
        double avg = (double) runs / (double) counter.size();
        for (Integer count : counter.values()) {
            // count 必须处于 0 到 2 * avg 之间
            Assert.assertTrue(count < 2 * avg);
        }
    }

    @Test
    public void roundRobinTest() {
        for (int i = 0; i < 500; i++) {
            URL url = ROUND_ROBIN_LOAD_BALANCE.select(candidateUrls, RPC_REQUEST);
            Assert.assertEquals(candidateUrls.get(i % candidateUrls.size()), url);
        }
    }
}
