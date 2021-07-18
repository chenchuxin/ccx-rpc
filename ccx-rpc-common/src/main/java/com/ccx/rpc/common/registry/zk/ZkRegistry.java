package com.ccx.rpc.common.registry.zk;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncoder;
import com.ccx.rpc.common.consts.RegistryConst;
import com.ccx.rpc.common.consts.URLParamKeyConst;
import com.ccx.rpc.common.registry.Registry;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.common.url.URLParser;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * zk 注册中心。<br>
 * 这里面会引入 curator，最好是新建个 Module 的，不过现在代码还太简单，不需要
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
public class ZkRegistry implements Registry {

    private final CuratorZkClient zkClient;
    private static final URLEncoder urlEncoder = URLEncoder.createPathSegment();
    private static final Charset charset = Charset.defaultCharset();

    public ZkRegistry(URL url) {
        zkClient = new CuratorZkClient(url);
    }

    @Override
    public void register(URL url) {
        zkClient.createPersistentNode(toUrlPath(url, true));
    }

    @Override
    public void unregister(URL url) {
        zkClient.removeNode(toUrlPath(url, true));
    }

    @Override
    public List<URL> lookup(URL condition) {
        List<String> children = zkClient.getChildren(toServicePath(condition, true));
        return children.stream().map(s -> URLParser.toURL(URLDecoder.decode(s, charset)))
                .collect(Collectors.toList());
    }

    private String toUrlPath(URL url, boolean isProvider) {
        return toServicePath(url, isProvider) + "/" + urlEncoder.encode(url.toFullString(), charset);
    }

    private String toServicePath(URL url, boolean isProvider) {
        return url.getParam(URLParamKeyConst.INTERFACE_KEY, url.getPath())
                + "/" + (isProvider ? RegistryConst.PROVIDERS_CATEGORY : RegistryConst.CONSUMERS_CATEGORY);
    }

}
