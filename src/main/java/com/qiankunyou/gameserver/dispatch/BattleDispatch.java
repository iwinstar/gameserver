package com.qiankunyou.gameserver.dispatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.qiankunyou.gameserver.common.Keys;
import com.qiankunyou.gameserver.common.Config;
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
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * User: mengmeng.cheng
 * Date: 4/13/14
 * Time: 6:08 PM
 * Email: chengmengmeng@gmail.com
 */
public class BattleDispatch {
    private static final Logger logger = Logger.getLogger(BattleDispatch.class);

    /**
     * 分发客户端请求
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void dispatch(ChannelHandlerContext ctx, SocketRequest request) {
        if (request.getNumber() == ProtocolCode.BATTLE_GET_PLAYER) {
            logger.debug("Protocol BATTLE_GET_PLAYER message: " + request);
            getPlayer(ctx, request);
        } else if (request.getNumber() == ProtocolCode.BATTLE_UPLOAD_LAYOUT_INFO) {
            logger.debug("Protocol BATTLE_UPLOAD_LAYOUT_INFO message: " + request);
            uploadLayoutInfo(ctx, request);
        } else if (request.getNumber() == ProtocolCode.BATTLE_UPLOAD_ATTACK_INFO) {
            logger.debug("Protocol BATTLE_UPLOAD_ATTACK_INFO message: " + request);
            uploadAttackInfo(ctx, request);
        } else if (request.getNumber() == ProtocolCode.BATTLE_UPLOAD_ATTACK_RESULT) {
            logger.debug("Protocol BATTLE_UPLOAD_ATTACK_RESULT message: " + request);
            uploadAttackResult(ctx, request);
        } else {
            logger.error("unknown protocol code: " + request.getNumber() + ", message: " + request);
        }
    }

    /**
     * 获取对手
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void getPlayer(ChannelHandlerContext ctx, SocketRequest request) {
        String targetID = "0";
        String sourceID = String.valueOf(request.getSourceID());

        Map<String, String> map = RoomRedis.getInstance().getPlayer(sourceID);

        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.BATTLE_SEND_PLAYER);
        response.setResult(0);
        response.setValueMap(map);

        ctx.writeAndFlush(response);

        if (!targetID.equals("0")){
            response.setValue(Keys.ROOM_TARGET_ID, sourceID);
            SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);

            timerLayoutTimeOver(sourceID, targetID);
        } else {
            // 如果对手是AI，走AI的流程
            AiBattleDispatch.timerLayoutTimeOver(sourceID);
        }
    }

    /**
     * 处理客户端发送的布局信息并回传
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void uploadLayoutInfo(ChannelHandlerContext ctx, SocketRequest request) {
        final String sourceID = String.valueOf(request.getSourceID());
        final String targetID = String.valueOf(request.getTargetID());

        String roomID = UserRedis.getInstance().get(sourceID, Keys.USER_ROOM_ID);

        RoomRedis.getInstance().add(roomID, Keys.ROOM_LAYOUT_INFO_PREFIX + sourceID, JSON.toJSONString(request.getParamMap()));
        String targetLayoutInfo = RoomRedis.getInstance().get(roomID, Keys.ROOM_LAYOUT_INFO_PREFIX + targetID);

        if (targetLayoutInfo != null && !targetLayoutInfo.equals("")){
            RoomRedis.getInstance().del(roomID, Keys.ROOM_LAYOUT_INFO_PREFIX + sourceID);
            RoomRedis.getInstance().del(roomID, Keys.ROOM_LAYOUT_INFO_PREFIX + targetID);

            SocketResponse response = new SocketResponse();
            response.setNumber(ProtocolCode.BATTLE_SEND_LAYOUT_INFO);
            response.setResult(0);
            response.setValueMap(JSON.parseObject(targetLayoutInfo, new TypeReference<Map<String, String>>() {}));

            ctx.writeAndFlush(response);
            response.setValueMap(request.getParamMap());
            SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);

            sendAttackTimeStart(sourceID, targetID); // TODO 是否要发
            timerAttackTimeOver(sourceID, targetID);
        }
    }

    /**
     * 向双方发送开始攻击指令
     *
     * @param sourceID 用户ID
     * @param targetID 对手ID
     */
    private static void sendAttackTimeStart(String sourceID, String targetID) {
        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.BATTLE_SEND_ATTACK_START);
        response.setResult(0);
        response.setValueMap(null);

        SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
        SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
    }

    /**
     * 处理客户端上传攻击信息并回传
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void uploadAttackInfo(ChannelHandlerContext ctx, SocketRequest request) {
        String sourceID = String.valueOf(request.getSourceID());
        String targetID = String.valueOf(request.getTargetID());
        String roomID = UserRedis.getInstance().get(sourceID, Keys.USER_ROOM_ID);

        RoomRedis.getInstance().add(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + sourceID, JSON.toJSONString(request.getParamMap()));
        String targetAttackInfo = RoomRedis.getInstance().get(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + targetID);

        if (targetAttackInfo != null && !targetAttackInfo.equals("")){
            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + sourceID);
            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + targetID);

            SocketResponse response = new SocketResponse();
            response.setNumber(ProtocolCode.BATTLE_SEND_ATTACK_INFO);
            response.setResult(0);
            response.setValueMap(JSON.parseObject(targetAttackInfo, new TypeReference<Map<String, String>>() {}));

            ctx.writeAndFlush(response);

            response.setValueMap(request.getParamMap());
            SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
        }
    }

    /**
     * 处理客户端上传回合结果
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void uploadAttackResult(ChannelHandlerContext ctx, SocketRequest request) {
        String sourceID = String.valueOf(request.getSourceID());
        String targetID = String.valueOf(request.getTargetID());
        String roomID = UserRedis.getInstance().get(sourceID, Keys.USER_ROOM_ID);

        RoomRedis.getInstance().add(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + sourceID, JSON.toJSONString(request.getParamMap()));
        String targetAttackResult = RoomRedis.getInstance().get(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + targetID);

        if (targetAttackResult != null && !targetAttackResult.equals("")){
            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + sourceID);
            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + targetID);

            // 增加回合数
            RoomRedis.getInstance().add(roomID, Keys.ROOM_ROUND, 1);

            Map<String, String> targetParamMap = JSON.parseObject(targetAttackResult, new TypeReference<Map<String, String>>() {});
            int sourceRemain = Integer.parseInt(targetParamMap.get(Keys.ROOM_ATTACK_RESULT_PLAIN_REMAIN));
            int targetRemain = Integer.parseInt(request.getValue(Keys.ROOM_ATTACK_RESULT_PLAIN_REMAIN));

            if (sourceRemain != 0 && targetRemain != 0){
                sendAttackTimeStart(sourceID, targetID);
                timerAttackTimeOver(sourceID, targetID);
            } else {
                sendBattleResult(roomID, sourceID, targetID);
            }
        }
    }

    /**
     * 发送战斗结果
     *
     * @param roomID 房间ID
     * @param sourceID 用户ID
     * @param targetID 对手ID
     */
    public static void sendBattleResult(String roomID, String sourceID, String targetID){
        Map<String, String> sourceMap = JSON.parseObject(RoomRedis.getInstance().get(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + targetID), new TypeReference<Map<String, String>>() {});
        Map<String, String> targetMap = JSON.parseObject(RoomRedis.getInstance().get(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + sourceID), new TypeReference<Map<String, String>>() {});

        int sourceRemain = Integer.parseInt(sourceMap.get(Keys.ROOM_ATTACK_RESULT_PLAIN_REMAIN));
        int targetRemain = Integer.parseInt(targetMap.get(Keys.ROOM_ATTACK_RESULT_PLAIN_REMAIN));

        int sourceDegree = Integer.valueOf(UserRedis.getInstance().get(sourceID, Keys.USER_DEGREE));
        int targetDegree = Integer.valueOf(UserRedis.getInstance().get(targetID, Keys.USER_DEGREE));
        double sourceDegreeDiff = 1f + (sourceDegree - targetDegree) * 0.1;
        double targetDegreeDiff = 1f + (targetDegree - sourceDegree) * 0.1;

        int sourceFuelTotal = Integer.valueOf(sourceMap.get(Keys.ROOM_RESULT_FUEL_TOTAL));
        int sourceNotInjuredTotal = Integer.valueOf(sourceMap.get(Keys.ROOM_RESULT_NOT_INJURED_TOTAL));
        int targetFuelTotal = Integer.valueOf(targetMap.get(Keys.ROOM_RESULT_FUEL_TOTAL));
        int targetNotInjuredTotal = Integer.valueOf(targetMap.get(Keys.ROOM_RESULT_NOT_INJURED_TOTAL));
        int round = Integer.parseInt(RoomRedis.getInstance().get(roomID, Keys.ROOM_ROUND));

        int sourceScore;
        int targetScore;

        SocketResponse response = new SocketResponse();
        response.setNumber(ProtocolCode.BATTLE_SEND_RESULT);
        response.setResult(0);

        if (sourceRemain == targetRemain) {
            sourceScore = (int)((Config.CONSTANT_SCORE_A * sourceFuelTotal + Config.CONSTANT_SCORE_D * round) * sourceDegreeDiff);
            targetScore = (int)((Config.CONSTANT_SCORE_A * targetFuelTotal + Config.CONSTANT_SCORE_D * round) * targetDegreeDiff);

            response.setValue(Keys.BATTLE_RESULT, Keys.BATTLE_RESULT_DRAW);
            response.setValue(Keys.USER_SCORE, String.valueOf(sourceScore));
            SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);

            response.setValue(Keys.BATTLE_RESULT, Keys.BATTLE_RESULT_DRAW);
            response.setValue(Keys.USER_SCORE, String.valueOf(targetScore));
            SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
        } else if (sourceRemain > targetRemain) {
            sourceScore = (int)((Config.CONSTANT_SCORE_A * (sourceFuelTotal + sourceNotInjuredTotal) + Config.CONSTANT_SCORE_B / round) * sourceDegreeDiff);
            targetScore = (int)((Config.CONSTANT_SCORE_A * targetFuelTotal + Config.CONSTANT_SCORE_C * round) * targetDegreeDiff);

            response.setValue(Keys.BATTLE_RESULT, Keys.BATTLE_RESULT_WIN);
            response.setValue(Keys.USER_SCORE, String.valueOf(sourceScore));
            SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);

            response.setValue(Keys.BATTLE_RESULT, Keys.BATTLE_RESULT_FAIL);
            response.setValue(Keys.USER_SCORE, String.valueOf(targetScore));
            SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
        } else {
            sourceScore = (int)((Config.CONSTANT_SCORE_A * sourceFuelTotal + Config.CONSTANT_SCORE_C * round) * sourceDegreeDiff);
            targetScore = (int)((Config.CONSTANT_SCORE_A * (targetFuelTotal + targetNotInjuredTotal) + Config.CONSTANT_SCORE_B / round) * targetDegreeDiff);

            response.setValue(Keys.BATTLE_RESULT, Keys.BATTLE_RESULT_FAIL);
            response.setValue(Keys.USER_SCORE, String.valueOf(sourceScore));
            SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);

            response.setValue(Keys.BATTLE_RESULT, Keys.BATTLE_RESULT_WIN);
            response.setValue(Keys.USER_SCORE, String.valueOf(targetScore));
            SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
        }

        // 更新用户积分资料
        int sourceScoreTotal = UserRedis.getInstance().add(sourceID, Keys.USER_SCORE, sourceScore);
        int targetScoreTotal = UserRedis.getInstance().add(targetID, Keys.USER_SCORE, targetDegree);

        // 更新用户等级资料
        UserRedis.getInstance().add(sourceID, Keys.USER_DEGREE, StringUtil.getDegree(sourceScoreTotal));
        UserRedis.getInstance().add(targetID, Keys.USER_DEGREE, StringUtil.getDegree(targetScoreTotal));

        // 增加排行榜数据
        RankRedis.getInstance().add(sourceID, sourceScore);
        RankRedis.getInstance().add(targetID, targetScore);
    }

    /**
     * 布局时间到定时器
     *
     * @param sourceID 用户ID
     * @param targetID 对手ID
     */
    private static void timerLayoutTimeOver(final String sourceID, final String targetID) {
        // 启动布局时间定时器
        Timer timer = new HashedWheelTimer();
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                SocketResponse response = new SocketResponse();
                response.setNumber(ProtocolCode.BATTLE_SEND_LAYOUT_TIME_OVER);
                response.setResult(0);
                response.setValueMap(null);

                SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
                SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
            }
        }, Config.LAYOUT_TIMEOVER, TimeUnit.SECONDS);
    }

    /**
     * 攻击时间结束定时器
     *
     * @param sourceID 用户ID
     * @param targetID 对手ID
     */
    private static void timerAttackTimeOver(final String sourceID, final String targetID) {
        // 启动攻击时间定时器
        Timer timer = new HashedWheelTimer();
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                SocketResponse response = new SocketResponse();
                response.setNumber(ProtocolCode.BATTLE_SEND_ATTACK_TIME_OVER);
                response.setResult(0);
                response.setValueMap(null);

                SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
                SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
            }
        }, Config.ATTACK_TIMEOVER, TimeUnit.SECONDS);
    }
}
