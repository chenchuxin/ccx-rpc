package com.ccx.rpc.core.proxy;

import cn.hutool.core.lang.UUID;
import com.ccx.rpc.core.annotation.RpcReference;
import com.ccx.rpc.core.remoting.client.netty.NettyClient;
import com.ccx.rpc.core.remoting.dto.RpcRequest;
import com.ccx.rpc.core.remoting.dto.RpcResponse;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

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
     * @param <T>
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
                .requestId(UUID.fastUUID().toString())
                .version(rpcReference.version())
                .build();
        CompletableFuture<RpcResponse<?>> responseFuture = NettyClient.getInstance().sendRpcRequest(request);
        RpcResponse<?> rpcResponse = responseFuture.get();
        // TODO：自定义 RpcException
        return rpcResponse.getData();
    }

}
