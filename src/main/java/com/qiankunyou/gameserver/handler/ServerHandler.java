package com.qiankunyou.gameserver.handler;

import com.qiankunyou.gameserver.common.ProtocolCode;
import com.qiankunyou.gameserver.dispatch.AiBattleDispatch;
import com.qiankunyou.gameserver.dispatch.BattleDispatch;
import com.qiankunyou.gameserver.dispatch.SystemDispatch;
import com.qiankunyou.gameserver.dispatch.UserDispatch;
import com.qiankunyou.gameserver.domain.SocketRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.log4j.Logger;

/**
 * User: mengmeng.cheng
 * Date: 4/10/14
 * Time: 2:11 PM
 * Email: chengmengmeng@gmail.com
 */
public class ServerHandler extends SimpleChannelInboundHandler<SocketRequest> {

    private static final Logger logger = Logger.getLogger(ServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelActive: " + ctx.channel().remoteAddress());
//
//        SocketResponse response = new SocketResponse();
//
//        response.setValue("name", "server");
//
//        ctx.writeAndFlush(response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelInactive: " + ctx.channel().remoteAddress());

        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SocketRequest request) throws Exception {
        logger.debug("channel read: " + request.toString());

        // 按照协议规定分发客户端请求
        if (request.getNumber() >= ProtocolCode.USER_MIN && request.getNumber() <= ProtocolCode.USER_MAX){
            UserDispatch.dispatch(ctx, request);
        } else if (request.getNumber() >= ProtocolCode.BATTLE_MIN && request.getNumber() <= ProtocolCode.BATTLE_MAX){
            BattleDispatch.dispatch(ctx, request);
        } else if (request.getNumber() >= ProtocolCode.AI_BATTLE_MIN && request.getNumber() <= ProtocolCode.AI_BATTLE_MAX){
            AiBattleDispatch.dispatch(ctx, request);
        } else if (request.getNumber() >= ProtocolCode.SYSTEM_MIN && request.getNumber() <= ProtocolCode.SYSTEM_MAX){
            SystemDispatch.dispatch(ctx, request);
        } else {
            logger.error("unknown codec code: " + request.getNumber() + ", message: " + request);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exception caught: " + cause.getMessage());

        cause.printStackTrace();
        ctx.close();
    }
}
