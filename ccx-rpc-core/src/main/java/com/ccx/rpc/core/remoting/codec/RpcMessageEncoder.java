package com.ccx.rpc.core.remoting.codec;

import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.core.compress.Compressor;
import com.ccx.rpc.core.consts.SerializeType;
import com.ccx.rpc.core.consts.CompressType;
import com.ccx.rpc.core.consts.MessageFormatConst;
import com.ccx.rpc.core.consts.MessageType;
import com.ccx.rpc.core.dto.RpcMessage;
import com.ccx.rpc.core.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>
 * 自定义协议编码器
 * <p>
 * <pre>
 *   0     1     2       3    4    5    6    7           8        9        10   11   12   13   14   15   16   17   18
 *   +-----+-----+-------+----+----+----+----+-----------+---------+--------+----+----+----+----+----+----+----+---+
 *   |   magic   |version|    full length    |messageType|serialize|compress|              RequestId               |
 *   +-----+-----+-------+----+----+----+----+-----------+----- ---+--------+----+----+----+----+----+----+----+---+
 *   |                                                                                                             |
 *   |                                         body                                                                |
 *   |                                                                                                             |
 *   |                                        ... ...                                                              |
 *   +-------------------------------------------------------------------------------------------------------------+
 *   2B magic（魔数）
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
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        // 2B magic code（魔数）
        out.writeBytes(MessageFormatConst.MAGIC);
        // 1B version（版本）
        out.writeByte(MessageFormatConst.VERSION);
        // 4B full length（消息长度）. 总长度先空着，后面填。
        out.writerIndex(out.writerIndex() + MessageFormatConst.FULL_LENGTH_LENGTH);
        // 1B messageType（消息类型）
        out.writeByte(rpcMessage.getMessageType());
        // 1B codec（序列化类型）
        out.writeByte(rpcMessage.getSerializeType());
        // 1B compress（压缩类型）
        out.writeByte(rpcMessage.getCompressTye());
        // 8B requestId（请求的Id）
        out.writeLong(rpcMessage.getRequestId());
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
        if (messageType == MessageType.HEARTBEAT.getValue()) {
            return 0;
        }

        // 序列化器
        SerializeType serializeType = SerializeType.fromValue(rpcMessage.getSerializeType());
        if (serializeType == null) {
            throw new IllegalArgumentException("codec type not found");
        }
        Serializer serializer = ExtensionLoader.getLoader(Serializer.class).getExtension(serializeType.getName());

        // 压缩器
        CompressType compressType = CompressType.fromValue(rpcMessage.getCompressTye());
        Compressor compressor = ExtensionLoader.getLoader(Compressor.class).getExtension(compressType.getName());

        // 序列化
        byte[] notCompressBytes = serializer.serialize(rpcMessage.getData());
        // 压缩
        byte[] compressedBytes = compressor.compress(notCompressBytes);

        // 写 body
        out.writeBytes(compressedBytes);
        return compressedBytes.length;
    }
}
