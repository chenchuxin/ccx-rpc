package com.ccx.rpc.common.consts;

/**
 * @author chenchuxin
 * @date 2021/8/1
 */
public class RpcException extends RuntimeException {
    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

}
