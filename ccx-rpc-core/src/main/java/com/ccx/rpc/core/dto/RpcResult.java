package com.ccx.rpc.core.dto;

/**
 * @author chenchuxin
 * @date 2021/8/8
 */
public interface RpcResult {

    boolean isSuccess();

    Object getData();
}
