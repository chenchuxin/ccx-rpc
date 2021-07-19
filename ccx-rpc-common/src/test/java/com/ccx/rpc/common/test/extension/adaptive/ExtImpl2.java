package com.ccx.rpc.common.test.extension.adaptive;

import com.ccx.rpc.common.url.URL;

/**
 * @author chenchuxin
 * @date 2021/7/20
 */
public class ExtImpl2 implements Ext {

    private URL url;

    public ExtImpl2(URL url) {
        this.url = url;
    }

    @Override
    public void doing() {
        System.out.println("ExtImpl2:" + url.toFullString());
    }
}
