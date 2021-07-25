package com.ccx.rpc.core.consts;

import cn.hutool.core.util.ByteUtil;

import java.util.Arrays;

/**
 * 消息格式常量
 *
 * @author chenchuxin
 * @date 2021/7/25
 */
public interface MessageFormatConst {

    /**
     * 魔法数字
     */
    byte[] MAGIC_NUMBER = ByteUtil.numberToBytes((short) 0x52ff);

    /**
     * 魔法数字长度
     */
    int MAGIC_LENGTH = 2;

    /**
     * 版本
     */
    byte VERSION = 1;

    /**
     * 版本长度
     */
    int VERSION_LENGTH = 2;

    /**
     * 总长度字段的长度
     */
    int FULL_LENGTH_LENGTH = 4;

    /**
     * 请求头长度
     */
    int HEADER_LENGTH = 18;
}
