package com.ccx.rpc.common.test.extension.adaptive;

import com.ccx.rpc.common.url.URL;

/**
 * @author chenchuxin
 * @date 2021/7/20
 */
public class ExtFactoryImpl2 implements ExtFactory {

    public Ext getExt(URL url) {
        return new ExtImpl2(url);
    }
}
