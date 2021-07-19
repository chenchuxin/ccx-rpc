package com.ccx.rpc.common.test.extension.adaptive;

import com.ccx.rpc.common.url.URL;

/**
 * @author chenchuxin
 * @date 2021/7/20
 */
public class ExtImpl1 implements Ext {

    private URL url;

    public ExtImpl1(URL url) {
        this.url = url;
    }

    @Override
    public void doing() {
        System.out.println("ExtImpl1:" + url.toFullString());
    }
}
