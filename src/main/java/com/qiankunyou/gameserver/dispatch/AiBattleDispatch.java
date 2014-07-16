package com.qiankunyou.gameserver.dispatch;

import com.qiankunyou.gameserver.common.Config;
import com.qiankunyou.gameserver.common.Keys;
import com.qiankunyou.gameserver.common.ProtocolCode;
import com.qiankunyou.gameserver.common.StringUtil;
import com.qiankunyou.gameserver.manager.SocketManager;
import com.qiankunyou.gameserver.domain.SocketRequest;
import com.qiankunyou.gameserver.domain.SocketResponse;
import com.qiankunyou.gameserver.redis.RankRedis;
import com.qiankunyou.gameserver.redis.RoomRedis;
import com.qiankunyou.gameserver.redis.UserRedis;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.*;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * User: mengmeng.cheng
 * Date: 4/13/14
 * Time: 6:08 PM
 * Email: chengmengmeng@gmail.com
 */
public class AiBattleDispatch {
    private static final Logger logger = Logger.getLogger(AiBattleDispatch.class);

    /**
     * 分发客户端请求
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void dispatch(ChannelHandlerContext ctx, SocketRequest request){
        if (request.getNumber() == ProtocolCode.AI_BATTLE_UPLOAD_ATTACK_RESULT) {
            uploadAttackResult(ctx, request);
        } else {
            logger.error("unknown protocol code: " + request.getNumber() + ", message: " + request);
        }
    }

    /**
     * 处理客户端上传攻击结果
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void uploadAttackResult(ChannelHandlerContext ctx, SocketRequest request) {
        String sourceID = String.valueOf(request.getSourceID());
        String roomID = UserRedis.getInstance().get(sourceID, Keys.USER_ROOM_ID);

        int sourceRemain = Integer.parseInt(request.getValue(Keys.ROOM_ATTACK_RESULT_PLAIN_REMAIN));
        int targetRemain = Integer.parseInt(request.getValue(Keys.ROOM_ATTACK_RESULT_AI_PLAIN_REMAIN));

        if (sourceRemain != 0 && targetRemain != 0){
            RoomRedis.getInstance().add(roomID, Keys.ROOM_ROUND, 1);

            sendAttackTimeStart(sourceID);
            timerAttackTimeOver(sourceID);
        } else {
            sendBattleResult(roomID, sourceID, request);
        }
    }

    /**
     * 向客户端发送战斗结果
     *
     * @param roomID 房间ID
     * @param sourceID 用户ID
     * @param request 请求指令
     */
    public static void sendBattleResult(String roomID, String sourceID, SocketRequest request){
        int sourceRemain = Integer.parseInt(request.getValue(Keys.ROOM_ATTACK_RESULT_PLAIN_REMAIN));
        int targetRemain = Integer.parseInt(request.getValue(Keys.ROOM_ATTACK_RESULT_AI_PLAIN_REMAIN));

        int sourceDegree = Integer.valueOf(RoomRedis.getInstance().get(roomID, Keys.ROOM_SOURCE_DEGREE));
        int targetDegree = Integer.valueOf(RoomRedis.getInstance().get(roomID, Keys.ROOM_TARGET_DEGREE));
        double sourceDegreeDiff = 1f + (sourceDegree - targetDegree) * 0.1;

        int sourceFuelTotal = Integer.valueOf(request.getValue(Keys.ROOM_RESULT_FUEL_TOTAL));
        int sourceNotInjuredTotal = Integer.valueOf(request.getValue(Keys.ROOM_RESULT_NOT_INJURED_TOTAL));

        int round = Integer.valueOf(RoomRedis.getInstance().get(roomID, Keys.ROOM_ROUND));
        int sourceScore;

        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.AI_BATTLE_SEND_RESULT);
        response.setResult(0);

        if (sourceRemain == targetRemain) {
            sourceScore = (int)((Config.CONSTANT_SCORE_A * sourceFuelTotal + Config.CONSTANT_SCORE_D * round) * sourceDegreeDiff);

            response.setValue(Keys.BATTLE_RESULT, Keys.BATTLE_RESULT_DRAW);
            response.setValue(Keys.USER_SCORE, String.valueOf(sourceScore));
            SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
        } else if (sourceRemain > targetRemain) {
            sourceScore = (int)((Config.CONSTANT_SCORE_A * (sourceFuelTotal + sourceNotInjuredTotal) + Config.CONSTANT_SCORE_B / round) * sourceDegreeDiff);

            response.setValue(Keys.BATTLE_RESULT, Keys.BATTLE_RESULT_WIN);
            response.setValue(Keys.USER_SCORE, String.valueOf(sourceScore));
            SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
        } else {
            sourceScore = (int)((Config.CONSTANT_SCORE_A * sourceFuelTotal + Config.CONSTANT_SCORE_C * round) * sourceDegreeDiff);

            response.setValue(Keys.BATTLE_RESULT, Keys.BATTLE_RESULT_FAIL);
            response.setValue(Keys.USER_SCORE, String.valueOf(sourceScore));
            SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
        }

        // 更新用户积分资料
        int sourceScoreTotal = UserRedis.getInstance().add(sourceID, Keys.USER_SCORE, sourceScore);

        // 更新用户等级资料
        UserRedis.getInstance().add(sourceID, Keys.USER_DEGREE, StringUtil.getDegree(sourceScoreTotal));

        // 增加排行榜数据
        RankRedis.getInstance().add(sourceID, sourceScore);
    }

    /**
     * 布局时间到定时器
     *
     * @param sourceID 用户ID
     */
    public static void timerLayoutTimeOver(final String sourceID) {
        // 启动布局时间定时器
        io.netty.util.Timer timer = new HashedWheelTimer();
        timer.newTimeout(new io.netty.util.TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                SocketResponse response = new SocketResponse();

                response.setNumber(ProtocolCode.AI_BATTLE_SEND_LAYOUT_TIME_OVER);
                response.setResult(0);
                response.setValueMap(null);

                SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);

                sendAttackTimeStart(sourceID); // TODO 是否要发
                timerAttackTimeOver(sourceID);
            }
        }, Config.LAYOUT_TIMEOVER, TimeUnit.SECONDS);
    }

    /**
     * 攻击时间到定时器
     *
     * @param sourceID 用户ID
     */
    private static void timerAttackTimeOver(final String sourceID) {
        // 启动攻击时间定时器
        io.netty.util.Timer timer = new HashedWheelTimer();
        timer.newTimeout(new io.netty.util.TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                SocketResponse response = new SocketResponse();
                response.setNumber(ProtocolCode.AI_BATTLE_SEND_ATTACK_TIME_OVER);
                response.setResult(0);
                response.setValueMap(null);

                SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
            }
        }, Config.ATTACK_TIMEOVER, TimeUnit.SECONDS);
    }

    /**
     * 向客户端发送开始攻击指令
     *
     * @param sourceID 用户ID
     */
    public static void sendAttackTimeStart(String sourceID) {
        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.AI_BATTLE_SEND_ATTACK_START);
        response.setResult(0);
        response.setValueMap(null);

        SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
    }
}
