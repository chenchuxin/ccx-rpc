package com.ccx.rpc.demo.client.spi;

import cn.hutool.json.JSONUtil;

/**
 * @author chenchuxin
 * @date 2021/8/10
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        return JSONUtil.toJsonStr(object).getBytes();
    }
}