package com.ccx.rpc.common.test.url;

import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.common.url.URLParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenchuxin
 * @date 2021/7/18
 * @see URLParser
 */
public class URLParserTest {

    private URL url;

    @Before
    public void setup() {
        Map<String, String> params = new HashMap<>();
        params.put("timeout", "1000");
        params.put("param2", "value2");
        params.put("key3", "value3");
        url = URL.builder().protocol("zk")
                .username("user").password("pwd")
                .host("localhost").port(1234)
                .path("path").params(params).build();
    }

    @Test
    public void parseToStrTest() {
        String urlStr = URLParser.parseToStr(url, true, true);
        Assert.assertEquals("zk://user:pwd@localhost:1234/path?key3=value3&param2=value2&timeout=1000", urlStr);
    }

    @Test
    public void toURLTest() {
        String urlStr = "zk://user:pwd@localhost:1234/path?key3=value3&param2=value2&timeout=1000";
        URL url = URLParser.toURL(urlStr);
        Assert.assertEquals(urlStr, url.toFullString());
        Assert.assertEquals("zk", url.getProtocol());
        Assert.assertEquals("user", url.getUsername());
        Assert.assertEquals("pwd", url.getPassword());
        Assert.assertEquals("localhost", url.getHost());
        Assert.assertEquals(1234, url.getPort());
        Assert.assertEquals("path", url.getPath());
        Assert.assertEquals("1000", url.getParam("timeout", null));
        Assert.assertEquals("value2", url.getParam("param2", null));
        Assert.assertEquals("value3", url.getParam("key3", null));
    }
}
