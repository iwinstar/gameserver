package com.qiankunyou.game.client;

import com.qiankunyou.gameserver.domain.SocketRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.log4j.Logger;

/**
 * User: mengmeng.cheng
 * Date: 4/10/14
 * Time: 5:27 PM
 * Email: chengmengmeng@gmail.com
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger logger=Logger.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String response) throws Exception {
        logger.info("channelRead0: " + response.toString());

        SocketRequest request = new SocketRequest();

        request.setLength(20);
        request.setNumber(2000);
        request.setSequence(100);
        request.setReserve(0);
        request.setSourceID(100);
        request.setTargetID(999);
        request.setValue("name", "client");

        ctx.writeAndFlush(request);
    }
}
