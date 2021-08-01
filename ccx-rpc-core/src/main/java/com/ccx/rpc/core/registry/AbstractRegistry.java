package com.ccx.rpc.core.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.lang.Assert;
import com.ccx.rpc.common.url.URL;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author chenchuxin
 * @date 2021/7/24
 */
@Slf4j
public abstract class AbstractRegistry implements Registry {

    private final Set<URL> registered = new ConcurrentHashSet<>();

    /**
     * 向注册中心注册服务
     *
     * @param url 注册者的信息
     */
    protected abstract void doRegister(URL url);

    /**
     * 向注册中心取消注册服务
     *
     * @param url 注册者的信息
     */
    protected abstract void doUnregister(URL url);

    /**
     * 查找注册的服务
     *
     * @param condition 查询条件
     * @return 符合查询条件的所有注册者
     */
    protected abstract List<URL> doLookup(URL condition);

    /**
     * 获取本机注册的所有 URL
     *
     * @return 不可修改的 Set
     */
    public Set<URL> getRegistered() {
        return Collections.unmodifiableSet(registered);
    }

    /**
     * 向注册中心注册服务
     *
     * @param url 注册者的信息
     */
    @Override
    public void register(URL url) {
        Assert.notNull(url, "register url == null");
        doRegister(url);
        registered.add(url);
        log.info("register: {}", url);
    }

    /**
     * 向注册中心取消注册服务
     *
     * @param url 注册者的信息
     */
    @Override
    public void unregister(URL url) {
        Assert.notNull(url, "register url == null");
        doUnregister(url);
        registered.remove(url);
        log.info("unregister: {}", url);
    }

    /**
     * 查找注册的服务
     *
     * @param condition 查询条件
     * @return 符合查询条件的所有注册者
     */
    @Override
    public List<URL> lookup(URL condition) {
        List<URL> urls = doLookup(condition);
        log.info("lookup: {}", urls);
        return urls;
    }
}
