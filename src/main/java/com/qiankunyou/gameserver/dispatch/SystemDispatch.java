package com.qiankunyou.gameserver.dispatch;

import com.qiankunyou.gameserver.common.Config;
import com.qiankunyou.gameserver.common.ConfigUtil;
import com.qiankunyou.gameserver.common.Keys;
import com.qiankunyou.gameserver.common.ProtocolCode;
import com.qiankunyou.gameserver.domain.SocketRequest;
import com.qiankunyou.gameserver.domain.SocketResponse;
import com.qiankunyou.gameserver.manager.SocketManager;
import com.qiankunyou.gameserver.store.HeartbeatStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * User: mengmeng.cheng
 * Date: 4/13/14
 * Time: 2:52 PM
 * Email: chengmengmeng@gmail.com
 */
public class SystemDispatch {
    private static final Logger logger = Logger.getLogger(SystemDispatch.class);

    /**
     * 分发客户端请求
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void dispatch(ChannelHandlerContext ctx, SocketRequest request){
        if (request.getNumber() == ProtocolCode.SYSTEM_GET_INFO){
            logger.debug("Protocol SYSTEM_GET_INFO message: " + request);
            getSystemInfo(ctx);
        } else if (request.getNumber() == ProtocolCode.SYSTEM_UPLOAD_HEARTBEAT) {
            logger.debug("Protocol SYSTEM_UPLOAD_HEARTBEAT message: " + request);
            heartBeat(ctx, request);
        } else if (request.getNumber() == ProtocolCode.SYSTEM_RELOAD_CONFIG){
            logger.debug("Protocol SYSTEM_RELOAD_CONFIG message: " + request);
            reloadConfig(ctx);
        } else {
            logger.error("unknown codec code: " + request.getNumber() + ", message: " + request);
        }
    }

    /**
     * 处理获取系统配置信息
     *
     * @param ctx ctx
     */
    public static void getSystemInfo(ChannelHandlerContext ctx){
        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put(Keys.HEARTBEAT_INTERVAL, String.valueOf(Config.HEARTBEAT_INTERVAL));

        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.SYSTEM_SEND_INFO);
        response.setResult(0);
        response.setValueMap(valueMap);

        ctx.writeAndFlush(response);
    }

    /**
     * 处理心跳请求
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void heartBeat(ChannelHandlerContext ctx, final SocketRequest request) {
        String userID = String.valueOf(request.getSourceID());

        HeartbeatStore.get(userID).stop();
        HeartbeatStore.remove(userID);

        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.SYSTEM_SEND_HEARTBEAT);
        response.setResult(0);
        response.setValueMap(null);

        ctx.writeAndFlush(response);

        timerSendHeartbeat(userID, String.valueOf(request.getTargetID()));
    }

    /**
     * 心跳超时定时器
     *
     * @param sourceID 用户ID
     * @param targetID 对手ID
     */
    private static void timerSendHeartbeat(final String sourceID, final String targetID){
        Timer timer = new HashedWheelTimer();
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                SocketManager.getDefaultStore().get(sourceID).fireChannelInactive();
                SocketManager.getDefaultStore().get(sourceID).close();
                SocketManager.getDefaultStore().remove(sourceID);

                if (!targetID.equals("0")){
                    SocketResponse response = new SocketResponse();
                    response.setNumber(ProtocolCode.BATTLE_SEND_PLAYER_OFFLINE);
                    response.setResult(0);
                    response.setValueMap(null);

                    SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
                }
            }
        }, Config.HEARTBEAT_TIMEOUT, TimeUnit.SECONDS);

        HeartbeatStore.add(sourceID, timer);
    }

    /**
     * 重新加载配置文件
     */
    public static void reloadConfig(ChannelHandlerContext ctx) {
        ConfigUtil.init();

        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.SYSTEM_SEND_RELOAD_CONFIG);
        response.setResult(0);
        response.setValue(Keys.SYSTEM_STATUS, Keys.SYSTEM_STATUS_OK);

        ctx.writeAndFlush(response);
    }
}
