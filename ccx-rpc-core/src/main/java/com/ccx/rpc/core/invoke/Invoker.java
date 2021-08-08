package com.ccx.rpc.core.invoke;

import com.ccx.rpc.common.consts.RpcException;
import com.ccx.rpc.common.extension.SPI;
import com.ccx.rpc.core.dto.RpcRequest;
import com.ccx.rpc.core.dto.RpcResult;

/**
 * 执行者
 *
 * @author chenchuxin
 * @date 2021/8/8
 */
@SPI("netty")
public interface Invoker {

    /**
     * 执行
     *
     * @param request 请求
     * @return result
     * @throws RpcException 执行异常会抛出
     */
    RpcResult invoke(RpcRequest request) throws RpcException;
}
