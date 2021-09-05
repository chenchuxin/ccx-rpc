package com.ccx.rpc.core.consts;

import cn.hutool.core.util.ByteUtil;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 消息格式常量
 *
 * @author chenchuxin
 * @date 2021/7/25
 */
public interface MessageFormatConst {

    /**
     * 魔数
     */
    byte[] MAGIC = ByteUtil.numberToBytes((short) 0x52ff);

    /**
     * 版本
     */
    byte VERSION = 1;

    /**
     * 请求 Id
     */
    AtomicLong REQUEST_ID = new AtomicLong(0);

    String PING_DATA = "ping";
    String PONG_DATA = "pong";

    /**
     * 魔法数字长度
     */
    int MAGIC_LENGTH = 2;

    /**
     * 版本长度
     */
    int VERSION_LENGTH = 1;

    /**
     * 总长度字段的长度
     */
    int FULL_LENGTH_LENGTH = 4;

    /**
     * 消息类型长度
     */
    int MESSAGE_TYPE_LENGTH = 1;

    /**
     * 编解码类型长度
     */
    int CODEC_LENGTH = 1;

    /**
     * 压缩器类型长度
     */
    int COMPRESS_LENGTH = 1;

    /**
     * 请求id 长度
     */
    int REQUEST_ID_LENGTH = 8;

    /**
     * 请求头长度
     */
    int HEADER_LENGTH = MAGIC_LENGTH + VERSION_LENGTH + FULL_LENGTH_LENGTH + MESSAGE_TYPE_LENGTH + CODEC_LENGTH + CODEC_LENGTH + REQUEST_ID_LENGTH;

    /**
     * 协议最大长度
     */
    int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}
