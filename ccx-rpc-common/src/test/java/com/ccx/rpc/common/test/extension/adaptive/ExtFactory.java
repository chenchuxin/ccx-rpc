package com.ccx.rpc.common.test.extension.adaptive;

import com.ccx.rpc.common.consts.URLKeyConst;
import com.ccx.rpc.common.extension.Adaptive;
import com.ccx.rpc.common.extension.SPI;
import com.ccx.rpc.common.url.URL;

/**
 * @author chenchuxin
 * @date 2021/7/20
 */
@SPI
public interface ExtFactory {

    @Adaptive(URLKeyConst.PROTOCOL)
    Ext getExt(URL url);
}
