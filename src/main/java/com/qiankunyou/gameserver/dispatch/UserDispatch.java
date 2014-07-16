package com.qiankunyou.gameserver.dispatch;

import com.qiankunyou.gameserver.common.Keys;
import com.qiankunyou.gameserver.common.ProtocolCode;
import com.qiankunyou.gameserver.domain.User;
import com.qiankunyou.gameserver.manager.SocketManager;
import com.qiankunyou.gameserver.domain.SocketRequest;
import com.qiankunyou.gameserver.domain.SocketResponse;
import com.qiankunyou.gameserver.redis.RankRedis;
import com.qiankunyou.gameserver.redis.UserRedis;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * User: mengmeng.cheng
 * Date: 4/13/14
 * Time: 1:58 PM
 * Email: chengmengmeng@gmail.com
 */
public class UserDispatch {
    private static final Logger logger = Logger.getLogger(UserDispatch.class);

    /**
     * 分发客户端请求
     *
     * @param ctx ctx
     * @param request request
     */
    public static void dispatch(ChannelHandlerContext ctx, SocketRequest request){
        if (request.getNumber() == ProtocolCode.USER_GET_INFO){
            logger.debug("Protocol USER_GET_INFO message: " + request);
            getUserByOpenInfo(ctx, request);
        } else if (request.getNumber() == ProtocolCode.USER_CHANGE_INFO){
            logger.debug("Protocol USER_CHANGE_INFO message: " + request);
            alterUser(ctx, request);
        } else if (request.getNumber() == ProtocolCode.USER_GET_RANKING) {
            logger.debug("Protocol USER_GET_RANKING message: " + request);
            getRanking(ctx, request);
        } else {
            logger.error("unknown protocol code: " + request.getNumber() + ", message: " + request);
        }
    }

    /**
     * 根据开放平台信息返回服务端用户数据
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void getUserByOpenInfo(ChannelHandlerContext ctx, SocketRequest request) {
        Map<String, String> map = UserRedis.getInstance().getUserByOpenInfo(User.fromMap(request.getParamMap()));

        // 存储此在线用户的ctx
        SocketManager.getDefaultStore().add(map.get(Keys.USER_ID), ctx);

        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.USER_SEND_INFO);
        response.setResult(0);
        response.setValueMap(map);

        ctx.writeAndFlush(response);
    }

    /**
     * 处理客户端修改信息指令
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void alterUser(ChannelHandlerContext ctx, SocketRequest request) {
        UserRedis.getInstance().alter(String.valueOf(request.getSourceID()), request.getParamMap());

        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.USER_SEND_CHANGE_INFO);
        response.setResult(0);
        response.setValueMap(null);

        ctx.writeAndFlush(response);
    }

    /**
     * 处理客户端获取排行榜指令
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void getRanking(ChannelHandlerContext ctx, SocketRequest request) {
        Map<String, String> map = RankRedis.getInstance().getRanking(String.valueOf(request.getSourceID()));

        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.USER_SEND_RANKING);
        response.setResult(0);
        response.setValueMap(map);

        ctx.writeAndFlush(response);
    }
}
