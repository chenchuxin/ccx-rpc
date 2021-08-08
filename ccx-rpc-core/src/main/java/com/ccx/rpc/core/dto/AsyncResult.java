package com.ccx.rpc.core.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 异步结果
 *
 * @author chenchuxin
 * @date 2021/8/8
 */
@Slf4j
public class AsyncResult implements RpcResult {

    private final CompletableFuture<?> future;

    public AsyncResult(CompletableFuture<?> future) {
        this.future = future;
    }

    @Override
    public boolean isSuccess() {
        return !future.isCompletedExceptionally();
    }

    @Override
    public Object getData() {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("getData error.", e);
        }
        return null;
    }
}
