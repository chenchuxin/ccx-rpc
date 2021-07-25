package com.ccx.rpc.core.remoting.codec;

import cn.hutool.core.lang.Assert;
import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.core.compress.Compressor;
import com.ccx.rpc.core.consts.CodecType;
import com.ccx.rpc.core.consts.CompressorType;
import com.ccx.rpc.core.consts.MessageFormatConst;
import com.ccx.rpc.core.consts.MessageType;
import com.ccx.rpc.core.remoting.dto.RpcMessage;
import com.ccx.rpc.core.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * custom protocol decoder
 * <p>
 * <pre>
 *   0     1     2       3    4    5    6    7           8        9        10   11   12   13   14   15   16   17   18
 *   +-----+-----+-------+----+----+----+----+-----------+--------+--------+----+----+----+----+----+----+----+----+
 *   |   magic   |version|    full length    |messageType|  codec |compress|              RequestId                |
 *   +-----+-----+-------+----+----+----+----+-----------+--------+--------+----+----+----+----+----+----+----+----+
 *   |                                                                                                             |
 *   |                                         body                                                                |
 *   |                                                                                                             |
 *   |                                        ... ...                                                              |
 *   +-------------------------------------------------------------------------------------------------------------+
 * 2B magic code（魔法数）
 * 1B version（版本）
 * 4B full length（消息长度）
 * 1B messageType（消息类型）
 * 1B codec（序列化类型）
 * 1B compress（压缩类型）
 * 8B requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 *
 * @author chenchuxin
 * @date 2021/7/25
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    /**
     * 请求 id
     */
    private static final AtomicLong REQ_ID = new AtomicLong(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        // 2B magic code（魔法数）
        out.writeBytes(MessageFormatConst.MAGIC_NUMBER);
        // 1B version（版本）
        out.writeByte(MessageFormatConst.VERSION);
        // 4B full length（消息长度）. 总长度先空着，后面填。
        out.writerIndex(out.writerIndex() + MessageFormatConst.FULL_LENGTH_LENGTH);
        // 1B messageType（消息类型）
        out.writeByte(rpcMessage.getMessageType());
        // 1B codec（序列化类型）
        out.writeByte(rpcMessage.getCodec());
        // 1B compress（压缩类型）
        out.writeByte(rpcMessage.getCompress());
        // 8B requestId（请求的Id）
        out.writeLong(REQ_ID.getAndIncrement());
        // 写 body，返回 body 长度
        int bodyLength = writeBody(rpcMessage, out);

        // 当前写指针
        int writerIndex = out.writerIndex();
        out.writerIndex(MessageFormatConst.MAGIC_LENGTH + MessageFormatConst.VERSION_LENGTH);
        // 4B full length（消息长度）
        out.writeInt(MessageFormatConst.HEADER_LENGTH + bodyLength);
        // 写指针复原
        out.writerIndex(writerIndex);
    }

    /**
     * 写 body
     *
     * @return body 长度
     */
    private int writeBody(RpcMessage rpcMessage, ByteBuf out) {
        byte messageType = rpcMessage.getMessageType();
        // 如果是 ping、pong 心跳类型的，没有 body，直接返回头部长度
        if (messageType == MessageType.HEARTBEAT_PING.getValue()
                || messageType == MessageType.HEARTBEAT_PONG.getValue()) {
            return 0;
        }

        // 序列化器
        CodecType codecType = CodecType.fromValue(rpcMessage.getCodec());
        if (codecType == null) {
            throw new IllegalArgumentException("codec type not found");
        }
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecType.getName());

        // 压缩器
        CompressorType compressorType = CompressorType.fromValue(rpcMessage.getCompress());
        if (compressorType == null) {
            throw new IllegalArgumentException("compressor type not found");
        }
        Compressor compressor = ExtensionLoader.getExtensionLoader(Compressor.class).getExtension(compressorType.getName());

        // 序列化
        byte[] notCompressBytes = serializer.serialize(rpcMessage.getData());
        // 压缩
        byte[] compressedBytes = compressor.compress(notCompressBytes);

        // 写 body
        out.writeBytes(compressedBytes);
        return compressedBytes.length;
    }
}
