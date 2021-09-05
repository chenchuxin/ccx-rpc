package com.ccx.rpc.core.remoting.codec;

import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.core.compress.Compressor;
import com.ccx.rpc.core.consts.SerializeType;
import com.ccx.rpc.core.consts.CompressType;
import com.ccx.rpc.core.consts.MessageType;
import com.ccx.rpc.core.dto.RpcMessage;
import com.ccx.rpc.core.dto.RpcRequest;
import com.ccx.rpc.core.dto.RpcResponse;
import com.ccx.rpc.core.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.ccx.rpc.core.consts.MessageFormatConst.*;

/**
 * <p>
 * 自定义协议解码器
 * <p>
 * <pre>
 *   0   1   2       3   4   5   6   7           8        9        10   11  12  13  14  15  16  17  18
 *   +---+---+-------+---+---+---+---+-----------+---------+--------+---+---+---+---+---+---+---+---+
 *   | magic |version|  full length  |messageType|serialize|compress|           RequestId           |
 *   +---+---+-------+---+---+---+---+-----------+---------+--------+---+---+---+---+---+---+---+---+
 *   |                                                                                              |
 *   |                                         body                                                 |
 *   |                                                                                              |
 *   |                                        ... ...                                               |
 *   +----------------------------------------------------------------------------------------------+
 *   2B magic（魔法数）
 *   1B version（版本）
 *   4B full length（消息长度）
 *   1B messageType（消息类型）
 *   1B serialize（序列化类型）
 *   1B compress（压缩类型）
 *   8B requestId（请求的Id）
 *   body（object类型数据）
 * </pre>
 *
 * @author chenchuxin
 * @date 2021/7/25
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        super(
                // 最大的长度，如果超过，会直接丢弃
                MAX_FRAME_LENGTH,
                // 描述长度的字段[4B full length（消息长度）]在哪个位置：在 [2B magic（魔数）]、[1B version（版本）] 后面
                MAGIC_LENGTH + VERSION_LENGTH,
                // 描述长度的字段[4B full length（消息长度）]本身的长度，也就是 4B 啦
                FULL_LENGTH_LENGTH,
                // LengthFieldBasedFrameDecoder 拿到消息长度之后，还会加上 [4B full length（消息长度）] 字段前面的长度
                // 因为我们的消息长度包含了这部分了，所以需要减回去
                -(MAGIC_LENGTH + VERSION_LENGTH + FULL_LENGTH_LENGTH),
                // initialBytesToStrip: 去除哪个位置前面的数据。因为我们还需要检测 魔数 和 版本号，所以不能去除
                0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= HEADER_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception ex) {
                    log.error("Decode frame error.", ex);
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    /**
     * 业务解码
     */
    private RpcMessage decodeFrame(ByteBuf in) {
        readAndCheckMagic(in);
        readAndCheckVersion(in);
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codec = in.readByte();
        byte compress = in.readByte();
        long requestId = in.readLong();

        RpcMessage rpcMessage = RpcMessage.builder()
                .serializeType(codec)
                .compressTye(compress)
                .requestId(requestId)
                .messageType(messageType)
                .build();

        if (messageType == MessageType.HEARTBEAT.getValue()) {
            return rpcMessage;
        }

        int bodyLength = fullLength - HEADER_LENGTH;
        if (bodyLength == 0) {
            return rpcMessage;
        }

        byte[] bodyBytes = new byte[bodyLength];
        in.readBytes(bodyBytes);
        // 解压
        CompressType compressType = CompressType.fromValue(compress);
        Compressor compressor = ExtensionLoader.getLoader(Compressor.class).getExtension(compressType.getName());
        byte[] decompressedBytes = compressor.decompress(bodyBytes);

        // 反序列化
        SerializeType serializeType = SerializeType.fromValue(codec);
        if (serializeType == null) {
            throw new IllegalArgumentException("unknown codec type:" + codec);
        }
        Serializer serializer = ExtensionLoader.getLoader(Serializer.class).getExtension(serializeType.getName());
        Class<?> clazz = messageType == MessageType.REQUEST.getValue() ? RpcRequest.class : RpcResponse.class;
        Object object = serializer.deserialize(decompressedBytes, clazz);
        rpcMessage.setData(object);
        return rpcMessage;
    }

    /**
     * 读取并检查版本
     */
    private void readAndCheckVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unknown version: " + version);
        }
    }

    /**
     * 读取并检查魔数
     */
    private void readAndCheckMagic(ByteBuf in) {
        byte[] bytes = new byte[MAGIC_LENGTH];
        in.readBytes(bytes);
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != MAGIC[i]) {
                throw new IllegalArgumentException("Unknown magic: " + Arrays.toString(bytes));
            }
        }
    }
}
