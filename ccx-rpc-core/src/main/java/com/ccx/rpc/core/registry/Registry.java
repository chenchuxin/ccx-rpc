package com.ccx.rpc.core.registry;

import com.ccx.rpc.common.url.URL;

import java.util.List;

/**
 * 注册中心
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
public interface Registry {

    /**
     * 向注册中心注册服务
     *
     * @param url 注册者的信息
     */
    void register(URL url);

    /**
     * 向注册中心取消注册服务
     *
     * @param url 注册者的信息
     */
    void unregister(URL url);

    /**
     * 查找注册的服务
     *
     * @param condition 查询条件
     * @return 符合查询条件的所有注册者
     */
    List<URL> lookup(URL condition);

    /**
     * 取消所有本机的服务，用于关机的时候
     */
    void unregisterAllMyService();
}
