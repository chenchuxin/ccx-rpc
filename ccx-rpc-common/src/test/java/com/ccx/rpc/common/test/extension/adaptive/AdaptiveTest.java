package com.ccx.rpc.common.test.extension.adaptive;

import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.common.url.URL;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author chenchuxin
 * @date 2021/7/20
 */
public class AdaptiveTest {

    @Test
    public void getAdaptiveExtendTest() {
        ExtensionLoader<ExtFactory> extensionLoader = ExtensionLoader.getLoader(ExtFactory.class);
        ExtFactory extFactory = extensionLoader.getAdaptiveExtension();
        Ext ext = extFactory.getExt(URL.valueOf("ext2://localhost:123"));
        Assert.assertTrue(ext instanceof ExtImpl2);
    }
}
