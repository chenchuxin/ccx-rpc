package com.ccx.rpc.common.registry.local;

import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.common.registry.Registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 本地注册中心，测试用
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
public class LocalRegistry implements Registry {

    private static final Set<URL> urls = new HashSet<>();

    @Override
    public void register(URL url) {
        urls.add(url);
    }

    @Override
    public void unregister(URL url) {
        urls.remove(url);
    }

    @Override
    public List<URL> lookup(URL condition) {
        return new ArrayList<>(urls);
    }
}
