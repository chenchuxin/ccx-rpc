package com.ccx.rpc.core.remoting.client.netty;

import cn.hutool.json.JSONUtil;
import com.ccx.rpc.core.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理的请求
 *
 * @author chenchuxin
 * @date 2021/7/31
 */
public class UnprocessedRequests {
    private static final Map<Long, CompletableFuture<RpcResponse<?>>> FUTURE_MAP = new ConcurrentHashMap<>();

    public static void put(long requestId, CompletableFuture<RpcResponse<?>> future) {
        FUTURE_MAP.put(requestId, future);
    }

    /**
     * 完成响应
     *
     * @param rpcResponse 响应内容
     */
    public static void complete(RpcResponse<?> rpcResponse) {
        CompletableFuture<RpcResponse<?>> future = FUTURE_MAP.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException("future is null. rpcResponse=" + JSONUtil.toJsonStr(rpcResponse));
        }
    }
}
