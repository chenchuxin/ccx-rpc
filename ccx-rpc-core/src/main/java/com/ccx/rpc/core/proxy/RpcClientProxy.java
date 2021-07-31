package com.ccx.rpc.core.proxy;

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

    /**
     * 获取代理类
     *
     * @param clazz 需要代理的接口类
     * @param <T>
     * @return 代理类
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return null;
    }

}
