package com.ccx.rpc.common.test.extension;

import com.ccx.rpc.common.extension.ExtensionLoader;
import org.junit.Assert;
import org.junit.Test;

/**
 * 类加载器测试
 *
 * @author chenchuxin
 * @date 2021/7/13
 */
public class ExtensionLoaderTest {

    @Test
    public void getExtensionLoaderTest() {
        ExtensionLoader<?> extensionLoader = ExtensionLoader.getExtensionLoader(Extension.class);
        Assert.assertNotNull(extensionLoader);
    }

    @Test
    public void getExtensionTest() {
        ExtensionLoader<?> extensionLoader = ExtensionLoader.getExtensionLoader(Extension.class);
        Extension extension = (Extension) extensionLoader.getExtension("other");
        Assert.assertTrue(extension instanceof OtherExtension);
    }

    @Test
    public void getDefaultExtensionTest() {
        ExtensionLoader<?> extensionLoader = ExtensionLoader.getExtensionLoader(Extension.class);
        Object extension = extensionLoader.getDefaultExtension();
        Assert.assertTrue(extension instanceof DefaultExtension);
    }

    @Test(expected = IllegalStateException.class)
    public void notInterfaceExtensionLoaderTest() {
        ExtensionLoader.getExtensionLoader(ExtensionNotInterface.class);
    }

    @Test(expected = IllegalStateException.class)
    public void notDefaultExtensionLoaderTest() {
        ExtensionLoader<?> extensionLoader = ExtensionLoader.getExtensionLoader(ExtensionNotDefault.class);
        extensionLoader.getDefaultExtension();
    }

    @Test(expected = IllegalStateException.class)
    public void notFileExtensionLoaderTest() {
        ExtensionLoader<?> extensionLoader = ExtensionLoader.getExtensionLoader(ExtensionNotFile.class);
        extensionLoader.getDefaultExtension();
    }
}
