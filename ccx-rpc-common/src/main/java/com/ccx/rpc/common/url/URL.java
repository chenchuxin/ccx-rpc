package com.ccx.rpc.common.url;

import cn.hutool.core.map.MapUtil;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

/**
 * url
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
@Builder
@Getter
public class URL {
    /**
     * 协议
     */
    private final String protocol;

    /**
     * 域名
     */
    private final String host;

    /**
     * 端口
     */
    private final int port;

    /**
     * 用户名
     */
    private final String username;

    /**
     * 密码
     */
    private final String password;

    /**
     * 路径
     */
    private final String path;

    /**
     * 参数
     */
    private final Map<String, String> params;

    // ====================== cache
    private String fullString;
    // ====================== end cache

    public Map<String, String> getParams() {
        if (params == null) {
            return Collections.emptyMap();
        }
        return params;
    }

    /**
     * 获取地址 host:port
     *
     * @return host:port
     */
    public String getAddress() {
        return host + ":" + port;
    }

    /**
     * 获取参数
     *
     * @param key        参数 key
     * @param defaultVal 默认值，如果获取不到，则用这个值
     * @return 参数，如果获取不到使用默认值
     */
    public String getParam(String key, String defaultVal) {
        return params.getOrDefault(key, defaultVal);
    }

    /**
     * 获取 int 类型的参数
     *
     * @param key        参数 key
     * @param defaultVal 默认值，如果获取不到，则用这个值
     * @return 参数，如果获取不到使用默认值
     */
    public int getIntParam(String key, int defaultVal) {
        if (MapUtil.isEmpty(params)) {
            return defaultVal;
        }
        String val = params.get(key);
        return val != null ? Integer.parseInt(val) : defaultVal;
    }

    public String toFullString() {
        if (fullString != null) {
            return fullString;
        }
        return fullString = URLParser.parseToStr(this, true, true);
    }

    public static URL valueOf(String url) {
        return URLParser.toURL(url);
    }
}
