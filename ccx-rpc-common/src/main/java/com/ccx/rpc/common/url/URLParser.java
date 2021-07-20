package com.ccx.rpc.common.url;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.util.*;

import static com.ccx.rpc.common.consts.URLKeyConst.DEFAULT_PREFIX;

/**
 * @author chenchuxin
 * @date 2021/7/18
 */
public class URLParser {

    /**
     * 转成字符串
     *
     * @param url             url对象
     * @param appendUser      是否加上用户密码
     * @param appendParameter 是否加上参数
     * @param parameters      参数，appendParameter=true 的时候有用
     * @return url 格式的字符串
     */
    public static String parseToStr(URL url, boolean appendUser, boolean appendParameter, String... parameters) {
        StringBuilder buf = new StringBuilder();
        if (StrUtil.isNotEmpty(url.getProtocol())) {
            buf.append(url.getProtocol());
            buf.append("://");
        }
        if (appendUser && StrUtil.isNotEmpty(url.getUsername())) {
            buf.append(url.getUsername());
            if (StrUtil.isNotEmpty(url.getPassword())) {
                buf.append(":");
                buf.append(url.getPassword());
            }
            buf.append("@");
        }
        if (StrUtil.isNotEmpty(url.getHost())) {
            buf.append(url.getHost());
            if (url.getPort() > 0) {
                buf.append(":");
                buf.append(url.getPort());
            }
        }
        if (StrUtil.isNotEmpty(url.getPath())) {
            buf.append("/");
            buf.append(url.getPath());
        }

        if (appendParameter) {
            buildParameters(url, buf, parameters);
        }
        return buf.toString();
    }

    private static void buildParameters(URL url, StringBuilder buf, String[] parameters) {
        List<String> includes = (ArrayUtil.isEmpty(parameters) ? null : Arrays.asList(parameters));
        boolean first = true;
        for (Map.Entry<String, String> entry : new TreeMap<>(url.getParams()).entrySet()) {
            if (StrUtil.isNotEmpty(entry.getKey())
                    && (includes == null || includes.contains(entry.getKey()))) {
                if (first) {
                    buf.append("?");
                    first = false;
                } else {
                    buf.append("&");
                }
                buf.append(entry.getKey());
                buf.append("=");
                buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
            }
        }
    }

    /**
     * 从字符串解析出 URL 对象。参考 dubbo
     *
     * @param url URL string
     * @return URL instance
     * @see URL
     */
    public static URL toURL(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String username = null;
        String password = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = null;
        int i = url.indexOf('?'); // separator between body and parameters
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("&");
            parameters = new HashMap<>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        String key = part.substring(0, j);
                        String value = part.substring(j + 1);
                        parameters.put(key, value);
                        // compatible with lower versions registering "default." keys
                        if (key.startsWith(DEFAULT_PREFIX)) {
                            parameters.putIfAbsent(key.substring(DEFAULT_PREFIX.length()), value);
                        }
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) {
                throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            }
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) {
                    throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                }
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf('/');
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.lastIndexOf('@');
        if (i >= 0) {
            username = url.substring(0, i);
            int j = username.indexOf(':');
            if (j >= 0) {
                password = username.substring(j + 1);
                username = username.substring(0, j);
            }
            url = url.substring(i + 1);
        }
        i = url.lastIndexOf(':');
        if (i >= 0 && i < url.length() - 1) {
            //noinspection StatementWithEmptyBody
            if (url.lastIndexOf('%') > i) {
                // ipv6 address with scope id
                // e.g. fe80:0:0:0:894:aeec:f37d:23e1%en0
                // see https://howdoesinternetwork.com/2013/ipv6-zone-id
                // ignore
            } else {
                port = Integer.parseInt(url.substring(i + 1));
                url = url.substring(0, i);
            }
        }
        if (url.length() > 0) {
            host = url;
        }

        return URL.builder().protocol(protocol).username(username).password(password)
                .host(host).port(port).path(path).params(parameters).build();
    }
}
