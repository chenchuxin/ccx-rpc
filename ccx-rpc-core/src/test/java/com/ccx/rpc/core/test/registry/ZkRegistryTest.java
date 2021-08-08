package com.ccx.rpc.core.test.registry;

import cn.hutool.core.net.NetUtil;
import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.core.registry.Registry;
import com.ccx.rpc.core.registry.RegistryFactory;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.common.url.URLParser;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * zk 注册中心测试
 *
 * @author chenchuxin
 * @date 2021/7/18
 */
public class ZkRegistryTest {

    private Registry registry;

    private TestingServer zkServer;

    private final URL serviceUrl = URLParser.toURL("zk://host:123/com.ccx.rpc.core.test.registry.ZkRegistryTest?notify=false&methods=test1,test2");
    private final URL serviceUrl2 = URLParser.toURL("zk://host2:123/com.ccx.rpc.core.test.registry.ZkRegistryTest?notify=false&methods=test1,test2");


    @Before
    public void setup() throws Exception {
        // 获取随机端口，绑定测试用的 zk 服务端
        int port = NetUtil.getUsableLocalPort();
        zkServer = new TestingServer(port, true);
        zkServer.start();
        URL url = URLParser.toURL("zk://localhost:" + port);
        RegistryFactory zkRegistryFactory = ExtensionLoader.getLoader(RegistryFactory.class).getAdaptiveExtension();
        registry = zkRegistryFactory.getRegistry(url);
    }

    @After
    public void stop() throws Exception {
        zkServer.stop();
    }

    @Test
    public void testRegister() {
        registry.register(serviceUrl);
        List<URL> lookup = registry.lookup(serviceUrl);
        Assert.assertEquals(1, lookup.size());
        Assert.assertEquals(serviceUrl.toFullString(), lookup.get(0).toFullString());

        registry.register(serviceUrl);
        lookup = registry.lookup(serviceUrl);
        Assert.assertEquals(1, lookup.size());

        registry.register(serviceUrl2);
        lookup = registry.lookup(serviceUrl);
        Assert.assertEquals(2, lookup.size());
    }

    @Test
    public void testUnregister() {
        {
            registry.register(serviceUrl);
            List<URL> lookup = registry.lookup(serviceUrl);
            Assert.assertEquals(1, lookup.size());
        }
        {
            registry.unregister(serviceUrl);
            List<URL> lookup = registry.lookup(serviceUrl);
            Assert.assertEquals(0, lookup.size());
        }
    }
}
