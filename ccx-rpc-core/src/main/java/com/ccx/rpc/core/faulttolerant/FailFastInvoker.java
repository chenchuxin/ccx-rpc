package com.ccx.rpc.core.faulttolerant;

import com.ccx.rpc.common.consts.RpcException;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.core.dto.RpcRequest;
import com.ccx.rpc.core.dto.RpcResult;
import com.ccx.rpc.core.invoke.Invoker;
import com.ccx.rpc.core.loadbalance.LoadBalance;

import java.util.List;

/**
 * 快速失败
 *
 * @author chenchuxin
 * @date 2021/8/8
 */
public class FailFastInvoker extends AbstractFaultTolerantInvoker {

    @Override
    protected RpcResult doInvoke(RpcRequest request, Invoker invoker, List<URL> candidateUrls, LoadBalance loadBalance) throws RpcException {
        return invoker.invoke(request);
    }
}
