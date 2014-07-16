package com.qiankunyou.game.client;

import com.qiankunyou.gameserver.codec.ProtocolUtil;
import com.qiankunyou.gameserver.domain.SocketRequest;

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
public class ClientEncoder extends MessageToByteEncoder<SocketRequest> {

    private static final Logger logger = Logger.getLogger(ClientEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, SocketRequest request, ByteBuf out) throws Exception {
        // cmdLen(指令长度) + cmdNum(指令编号) + cmdSeq(指令序号) + reserve(保留) + srcID(源角色ID) + targetID(对手角色ID) + data(指令体)

        logger.debug("encode request: " + request.toString());

        // 组织指令体
        ByteBuf dataBuffer = ProtocolUtil.encode(request.getParamMap());

        request.setLength(dataBuffer.readableBytes() + 20);

        // 组织指令头
        out.writeInt(request.getLength());
        out.writeInt(request.getNumber());
        out.writeInt(request.getSequence());
        out.writeInt(request.getReserve());
        out.writeInt(request.getSourceID());
        out.writeInt(request.getTargetID());

        out.writeBytes(dataBuffer);
    }
}
