package com.qiankunyou.game.client;

import com.qiankunyou.gameserver.codec.ProtocolUtil;
import com.qiankunyou.gameserver.domain.SocketResponse;

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
public class ClientDecoder extends ByteToMessageDecoder {

    private static final Logger logger = Logger.getLogger(ClientDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // cmdLen(指令长度) + cmdNum(指令编号) + cmdSeq(指令序号) + reserve(保留) + result(返回结果) + data(指令体)

        logger.info("enter decode");

        if (in.readableBytes() < 20) {
            return;
        }

        in.markReaderIndex();

        int length = in.readInt();
        int number = in.readInt();
        int sequence = in.readInt();
        int reserve = in.readInt();
        int result = in.readInt();

        if (in.readableBytes() != (length - 16)) {
            in.resetReaderIndex();
            return; // 指令数据长度不合法
        }

        ByteBuf dataBuffer = Unpooled.buffer(length - 16);
        in.readBytes(dataBuffer, length - 16);

        SocketResponse response = new SocketResponse();
        response.setLength(length);
        response.setNumber(number);
        response.setSequence(sequence);
        response.setReserve(reserve);
        response.setResult(result);
        response.setValueMap(ProtocolUtil.decode(dataBuffer));
        response.setIP(ProtocolUtil.getClientIP(ctx.channel()));

        out.add(response);
    }
}
