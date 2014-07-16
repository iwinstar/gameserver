package com.qiankunyou.gameserver.redis;

import com.qiankunyou.gameserver.common.Config;
import com.qiankunyou.gameserver.common.Keys;
import com.qiankunyou.gameserver.store.WaitingRoomStore;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * User: mengmeng.cheng
 * Date: 4/13/14
 * Time: 7:26 PM
 * Email: chengmengmeng@gmail.com
 */
public class RoomRedis {
    private static final RoomRedis instance = new RoomRedis();

    public static RoomRedis getInstance() {
        return instance;
    }

    /**
     * 获取房间属性值
     *
     * @param roomID 房间ID
     * @param key 字段
     * @return 字段值
     */
    public String get(String roomID, String key) {
        return RedisManager.hget(Keys.REDIS_PLAIN_GAME_ROOM_PREFIX + roomID, key);
    }

    /**
     * 增加房间属性值
     *
     * @param roomID 房间ID
     * @param field 字段
     * @param value 字段值
     */
    public void add(String roomID, String field, String value) {
        RedisManager.hset(Keys.REDIS_PLAIN_GAME_ROOM_PREFIX + roomID, field, value);
    }

    /**
     * 字段增加增量
     *
     * @param roomID 房间ID
     * @param field 字段
     * @param increment 增量
     */
    public void add(String roomID, String field, int increment) {
        RedisManager.hincrBy(Keys.REDIS_PLAIN_GAME_ROOM_PREFIX + roomID, field, increment);
    }

    /**
     * 删除字段
     *
     * @param roomID 房间ID
     * @param field 字段
     */
    public void del(String roomID, String field) {
        RedisManager.hdel(Keys.REDIS_PLAIN_GAME_ROOM_PREFIX + roomID, field);
    }

    /**
     * 获取对手
     *
     * @param userID 用户ID
     * @return 对手ID/AI
     */
    public Map<String, String> getPlayer(String userID) {
        String targetID;
        String degree = UserRedis.getInstance().get(userID, Keys.USER_DEGREE);
        String roomID = WaitingRoomRedis.getInstance().getRandomRoom(Integer.parseInt(degree));
        String targetDegree;

        if (roomID != null && !roomID.equals("")){
            targetID = RoomRedis.getInstance().get(roomID, Keys.ROOM_SOURCE_ID);
            targetDegree = RoomRedis.getInstance().get(roomID, Keys.ROOM_SOURCE_DEGREE);

            // 将用户的等级和ID数据录入
            RoomRedis.getInstance().add(roomID, Keys.ROOM_TARGET_ID, userID);
            RoomRedis.getInstance().add(roomID, Keys.ROOM_TARGET_DEGREE, degree);

            // 设置用户已经进入游戏
            UserRedis.getInstance().add(userID, Keys.USER_ROOM_ID, roomID);

            // 对方不用再等待，取消定时
            WaitingRoomStore.get(String.valueOf(targetID)).stop();
            WaitingRoomStore.remove(String.valueOf(targetID));
        } else {
            roomID = String.valueOf(SequenceRedis.getInstance().getNext(Keys.SEQUENCE_ROOM_ID));
            int aiDegree = Integer.parseInt(degree) + (int) Math.round(Math.random()*(2 * Config.PLAYER_DEGREE_DRIFT) - Config.PLAYER_DEGREE_DRIFT);

            WaitingRoomRedis.getInstance().add(Integer.parseInt(degree), roomID);

            // 将用户的ID和等级数据录入
            RoomRedis.getInstance().add(roomID, Keys.ROOM_SOURCE_ID, userID);
            RoomRedis.getInstance().add(roomID, Keys.ROOM_SOURCE_DEGREE, String.valueOf(degree));

            // 设置用户已经进入游戏
            UserRedis.getInstance().add(userID, Keys.USER_ROOM_ID, roomID);

            // 增加定时器
            timerWaitingPlayerTimeover(userID, roomID, String.valueOf(aiDegree));

            targetID = RoomRedis.getInstance().get(roomID, Keys.ROOM_TARGET_ID);
            targetDegree = RoomRedis.getInstance().get(roomID, Keys.ROOM_TARGET_DEGREE);
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put(Keys.ROOM_TARGET_ID, targetID);
        map.put(Keys.ROOM_TARGET_DEGREE, targetDegree);

        return map;
    }

    /**
     * 等待对手，超时后替换为AI
     *
     * @param userID 用户ID
     * @param roomID 房间ID
     * @param degree AI等级
     */
    private void timerWaitingPlayerTimeover(String userID, final String roomID, final String degree){
        Timer timer = new HashedWheelTimer();

        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                // 超时后，从等待队列中移除，设置AI为对方玩家
                WaitingRoomRedis.getInstance().del(roomID);

                // 设置对手数据为AI
                RoomRedis.getInstance().add(roomID, Keys.ROOM_TARGET_ID, "0");
                RoomRedis.getInstance().add(roomID, Keys.ROOM_TARGET_DEGREE, degree);
            }
        }, Config.PLAYER_TIMEOVER, TimeUnit.SECONDS);

        WaitingRoomStore.add(userID, timer);
    }
}
