package com.qiankunyou.gameserver.codec;

import com.qiankunyou.gameserver.domain.SocketResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.apache.log4j.Logger;

/**
 * User: mengmeng.cheng
 * Date: 4/10/14
 * Time: 12:55 PM
 * Email: chengmengmeng@gmail.com
 */
public class ServerEncoder extends MessageToByteEncoder<SocketResponse> {

    private static final Logger logger = Logger.getLogger(ServerEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SocketResponse response, ByteBuf out) throws Exception {


        // 回复指令：length(指令长度) + number(指令编号) + sequence(指令序号) + reserve(保留) + result(返回结果) + data(指令体)

        // 生成指令体
        ByteBuf dataBuffer = ProtocolUtil.encode(response.getValueMap());
        response.setLength(dataBuffer.readableBytes() + 16);

        logger.debug("enter encode: " + response.toString());

        // 输出指令头
        out.writeInt(response.getLength());
        out.writeInt(response.getNumber());
        out.writeInt(response.getSequence());
        out.writeInt(response.getReserve());
        out.writeInt(response.getResult());

        // 输出指令体
        out.writeBytes(dataBuffer);
    }
}
