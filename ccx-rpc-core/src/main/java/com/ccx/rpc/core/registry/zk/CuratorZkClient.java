package com.ccx.rpc.core.registry.zk;

import com.ccx.rpc.common.consts.URLKeyConst;
import com.ccx.rpc.common.url.URL;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * curator zk 客户端
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
@Slf4j
public class CuratorZkClient {
    /**
     * 默认连接超时毫秒数
     */
    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 5_000;
    /**
     * 默认 session 超时毫秒数
     */
    private static final int DEFAULT_SESSION_TIMEOUT_MS = 60_000;
    /**
     * session 超时时间
     */
    private static final String SESSION_TIMEOUT_KEY = "zk.sessionTimeoutMs";
    /**
     * 连接重试次数
     */
    private static final int RETRY_TIMES = 3;
    /**
     * 连接重试睡眠毫秒数
     */
    private static final int RETRY_SLEEP_MS = 1000;
    /**
     * 根目录
     */
    private static final String ROOT_PATH = "/ccx-rpc";
    /**
     * zk 客户端
     */
    private final CuratorFramework client;
    /**
     * 监听器 {path: 监听器}
     */
    private static final Map<String, CuratorCache> LISTENER_MAP = new ConcurrentHashMap<>();

    public CuratorZkClient(URL url) {
        int timeout = url.getIntParam(URLKeyConst.TIMEOUT, DEFAULT_CONNECTION_TIMEOUT_MS);
        int sessionTimeout = url.getIntParam(SESSION_TIMEOUT_KEY, DEFAULT_SESSION_TIMEOUT_MS);
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(url.getAddress())
                .retryPolicy(new RetryNTimes(RETRY_TIMES, RETRY_SLEEP_MS))
                .connectionTimeoutMs(timeout)
                .sessionTimeoutMs(sessionTimeout);
        if (StringUtils.isNotEmpty(url.getUsername()) || StringUtils.isNotEmpty(url.getPassword())) {
            String authority = StringUtils.defaultIfEmpty(url.getUsername(), "")
                    + ":" + StringUtils.defaultIfEmpty(url.getPassword(), "");
            builder.authorization("digest", authority.getBytes());
        }
        client = builder.build();
        client.start();
        try {
            client.blockUntilConnected(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Time out waiting to connect to zookeeper! Please check the zookeeper config.");
        }
    }

    /**
     * 创建节点
     *
     * @param path       路径，如果没有加上根目录，会自动加上根目录
     * @param createMode 节点模式
     */
    public void createNode(String path, CreateMode createMode) {
        try {
            client.create().creatingParentsIfNeeded().withMode(createMode).forPath(buildPath(path));
        } catch (KeeperException.NodeExistsException e) {
            log.warn("ZNode " + path + " already exists.");
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 创建永久节点
     *
     * @param path 路径，如果没有加上根目录，会自动加上根目录
     */
    public void createPersistentNode(String path) {
        createNode(path, CreateMode.PERSISTENT);
    }

    /**
     * 创建临时节点
     *
     * @param path 路径，如果没有加上根目录，会自动加上根目录
     */
    public void createEphemeralNode(String path) {
        createNode(path, CreateMode.EPHEMERAL);
    }

    /**
     * 删除节点
     *
     * @param path 路径，如果没有加上根目录，会自动加上根目录
     */
    public void removeNode(String path) {
        try {
            client.delete().forPath(buildPath(path));
        } catch (KeeperException.NoNodeException ignored) {
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 获取路径下的所有子节点
     *
     * @param path 要搜索的路径
     * @return 节点不存在返回空列表
     */
    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(buildPath(path));
        } catch (KeeperException.NoNodeException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 添加监听者
     *
     * @param path     相对路径
     * @param listener 监听者
     */
    public void addListener(String path, CuratorCacheListener listener) {
        String fullPath = buildPath(path);
        if (LISTENER_MAP.containsKey(fullPath)) {
            return;
        }
        CuratorCache curatorCache = CuratorCache.build(client, fullPath);
        LISTENER_MAP.put(fullPath, curatorCache);
        curatorCache.listenable().addListener(listener);
        curatorCache.start();
    }

    /**
     * 构建完整的路径，用于存 zk
     *
     * @param path 相对或者路径
     * @return 如果路径不包含根目录，加上根目录
     */
    private String buildPath(String path) {
        if (path.startsWith(ROOT_PATH)) {
            return path;
        }
        if (path.startsWith("/")) {
            return ROOT_PATH + path;
        }
        return ROOT_PATH + "/" + path;
    }
}
