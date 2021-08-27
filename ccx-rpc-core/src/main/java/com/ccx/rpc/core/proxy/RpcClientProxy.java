package com.ccx.rpc.core.proxy;

import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.core.annotation.RpcReference;
import com.ccx.rpc.core.config.ConfigManager;
import com.ccx.rpc.core.dto.RpcRequest;
import com.ccx.rpc.core.dto.RpcResponse;
import com.ccx.rpc.core.dto.RpcResult;
import com.ccx.rpc.core.faulttolerant.FaultTolerantInvoker;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Rpc 调用方的代理
 *
 * @author chenchuxin
 * @date 2021/7/31
 */
public class RpcClientProxy implements InvocationHandler {

    private final RpcReference rpcReference;

    public RpcClientProxy(RpcReference rpcReference) {
        this.rpcReference = rpcReference;
    }

    /**
     * 获取代理类
     *
     * @param clazz 需要代理的接口类
     * @param <T>   类型
     * @return 代理类
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        // TODO: 缓存，不然会生成很多代理类
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 调用 nettyClient 发请求
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .version(rpcReference.version())
                .build();
        ExtensionLoader<FaultTolerantInvoker> loader = ExtensionLoader.getLoader(FaultTolerantInvoker.class);
        FaultTolerantInvoker invoker = loader.getExtension(ConfigManager.getInstant().getClusterConfig().getFaultTolerant());
        RpcResult rpcResult = invoker.invoke(request);
        return ((RpcResponse<?>) rpcResult.getData()).getData();
    }

}
