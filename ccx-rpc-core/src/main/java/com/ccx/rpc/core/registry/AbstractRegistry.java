package com.ccx.rpc.core.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.lang.Assert;
import com.ccx.rpc.common.consts.URLKeyConst;
import com.ccx.rpc.common.url.URL;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.ccx.rpc.core.registry.RegistryEvent.Type.*;

/**
 * 抽象的注册中心
 *
 * @author chenchuxin
 * @date 2021/7/24
 */
@Slf4j
public abstract class AbstractRegistry implements Registry {

    /**
     * 已注册的服务的本地缓存。{serviceName: [URL]}
     */
    private final Map<String, Set<String>> registered = new ConcurrentHashMap<>();

    /**
     * 本机注册的服务
     */
    private static final Set<URL> myServiceURLs = new ConcurrentHashSet<>();

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
     * 向注册中心注册服务
     *
     * @param url 注册者的信息
     */
    @Override
    public void register(URL url) {
        Assert.notNull(url, "register url == null");
        doRegister(url);
        addToLocalCache(url);
        myServiceURLs.add(url);
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
        removeFromLocalCache(url);
        myServiceURLs.remove(url);
        log.info("unregister: {}", url);
    }

    /**
     * 查找注册的服务
     *
     * @param condition 查询条件，包含接口类型
     * @return 符合查询条件的所有注册者
     */
    @Override
    public List<URL> lookup(URL condition) {
        String serviceName = getServiceNameFromUrl(condition);
        if (registered.containsKey(serviceName)) {
            return registered.get(serviceName).stream().map(URL::valueOf).collect(Collectors.toList());
        }
        List<URL> urls = reset(condition);
        log.info("lookup: {}", urls);
        return urls;
    }

    /**
     * 取消所有本机的服务，用于关机的时候
     */
    @Override
    public void unregisterAllMyService() {
        log.info("unregisterAllMyService. myServiceURLs:{}", myServiceURLs);
        for (URL url : myServiceURLs) {
            unregister(url);
        }
    }

    /**
     * 重置。真实拿出注册信息，然后加到缓存中。
     *
     * @param condition
     * @return
     */
    public List<URL> reset(URL condition) {
        // 获取服务名
        String serviceName = getServiceNameFromUrl(condition);
        // 将原来注册信息本地缓存删掉
        registered.remove(serviceName);
        // 重新从注册中心获取
        List<URL> urls = doLookup(condition);
        for (URL url : urls) {
            // 将所有 Provider 添加到本地缓存
            addToLocalCache(url);
        }
        log.info("reset: {}", urls);
        return urls;
    }

    /**
     * 触发事件
     *
     * @param event 事件
     */
    public final void triggerEvent(RegistryEvent event) {
        RegistryEvent.Type type = event.getType();
        String data = event.getData();
        String oldData = event.getOldData();
        log.info("triggerEvent. event={}", event);
        if (type == CREATED) {
            // 新增节点
            if (data != null) {
                addToLocalCache(URL.valueOf(data));
            }
        } else if (type == DELETED) {
            if (oldData != null) {
                // 删除节点
                removeFromLocalCache(URL.valueOf(oldData));
            }
        } else if (type == CHANGED) {
            // 修改节点
            if (oldData != null) {
                removeFromLocalCache(URL.valueOf(oldData));
            }
            if (data != null) {
                addToLocalCache(URL.valueOf(data));
            }
        }
    }

    /**
     * 从 URL 中获取服务名
     */
    public String getServiceNameFromUrl(URL url) {
        return url.getParam(URLKeyConst.INTERFACE, url.getPath());
    }

    /**
     * 添加到本地缓存
     */
    private void addToLocalCache(URL url) {
        String serviceName = getServiceNameFromUrl(url);
        if (!registered.containsKey(serviceName)) {
            registered.put(serviceName, new ConcurrentHashSet<>());
        }
        registered.get(serviceName).add(url.toFullString());
    }

    /**
     * 从本地缓存中删除
     */
    private void removeFromLocalCache(URL url) {
        String serviceName = getServiceNameFromUrl(url);
        if (registered.containsKey(serviceName)) {
            registered.get(serviceName).remove(url.toFullString());
        }
    }
}
