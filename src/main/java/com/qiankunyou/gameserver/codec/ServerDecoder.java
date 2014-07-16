package com.qiankunyou.gameserver.codec;

import com.qiankunyou.gameserver.domain.SocketRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * User: mengmeng.cheng
 * Date: 4/10/14
 * Time: 1:50 PM
 * Email: chengmengmeng@gmail.com
 */
public class ServerDecoder extends ByteToMessageDecoder {

    private static final Logger logger = Logger.getLogger(ServerDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 请求指令：length(指令长度) + number(指令编号) + sequence(指令序号) + reserve(保留) + sourceID(源角色ID) + targetID(对手角色ID) + data(指令体)

        logger.debug("enter decode: " + in.toString());

        if (in.readableBytes() < 24) {
            return; // 指令头不合法
        }

        in.markReaderIndex();

        int length = in.readInt();
        int number = in.readInt();
        int sequence = in.readInt();
        int reserve = in.readInt();
        int sourceID = in.readInt();
        int targetID = in.readInt();

        if (in.readableBytes() != (length - 20)) {
            in.resetReaderIndex();
            return; // 指令体长度不合法
        }

        ByteBuf dataBuffer = Unpooled.buffer(length - 20);
        in.readBytes(dataBuffer, length - 20);

        SocketRequest request = new SocketRequest();
        request.setLength(length);
        request.setNumber(number);
        request.setSequence(sequence);
        request.setReserve(reserve);
        request.setSourceID(sourceID);
        request.setTargetID(targetID);
        request.setParamMap(ProtocolUtil.decode(dataBuffer));
        request.setIP(ProtocolUtil.getClientIP(ctx.channel()));

        out.add(request);
    }
}
