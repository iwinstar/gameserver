package com.qiankunyou.gameserver.redis;

import com.qiankunyou.gameserver.common.Keys;
import com.qiankunyou.gameserver.domain.User;

import java.util.Map;

/**
 * User: mengmeng.cheng
 * Date: 4/13/14
 * Time: 10:06 AM
 * Email: chengmengmeng@gmail.com
 */
public class UserRedis {
    private static final UserRedis instance = new UserRedis();

    public static UserRedis getInstance(){
        return instance;
    }

    /**
     * 根据开放平台的用户信息获取Redis中的用户信息
     *
     * @param user 用户信息
     * @return 用户信息Map
     */
    public Map<String, String> getUserByOpenInfo(User user) {
        // 使用 开放平台的ID+来源 查询 游戏平台ID
        String userIDStr = OpenUserRedis.getInstance().get(user.getOpenUserID(), user.getOpenSource());

        // 若此用户在服务器端不存在，创建之
        int userID = (userIDStr != null && !userIDStr.equals("")) ? Integer.parseInt(userIDStr) : UserRedis.getInstance().add(user);

        return UserRedis.getInstance().get(String.valueOf(userID));
    }

    /**
     * 根据用户ID获取Redis全量的用户数据
     *
     * @param userID 用户ID
     * @return 用户信息Map
     */
    public Map<String, String> get(String userID) {
        return RedisManager.hgetall(userID);
    }

    /**
     * 增加新用户
     *
     * @param user 用户信息
     * @return 用户ID
     */
    public int add(User user) {
        int userID = SequenceRedis.getInstance().getNext(Keys.SEQUENCE_USER_ID);
        user.setUserID(userID);

        // 设置用户相关数据
        UserRedis.getInstance().alter(String.valueOf(userID), User.toMap(user));

        // 存储开放平台ID+来源 与 游戏平台ID 对应关系
        OpenUserRedis.getInstance().add(user.getOpenUserID(), user.getOpenSource(), String.valueOf(userID));

        // 初始化排行榜数据
        RankRedis.getInstance().add(user.getScore(), String.valueOf(user));

        return userID;
    }

    /**
     * 修改用户数据
     *
     * @param userID 用户ID
     * @param field 字段名
     * @param value 字段值
     */
    public void add(String userID, String field, String value) {
        RedisManager.hset(Keys.REDIS_USER_PREFIX + userID, field, value);
    }

    /**
     * 增加属性
     *
     * @param userID 用户ID
     * @param key 字段
     * @param increment 增加量
     * @return 增加后的值
     */
    public int add(String userID, String key, int increment){
        return (int)RedisManager.hincrBy(Keys.REDIS_USER_PREFIX + userID, key, increment);
    }

    /**
     * 修改用户信息
     *
     * @param userID 用户ID
     * @param map 用户信息
     */
    public void alter(String userID, Map<String, String> map) {
        if (!map.isEmpty()) {
            for (String key : map.keySet()) {
                RedisManager.hset(Keys.REDIS_USER_PREFIX + userID, key, map.get(key));
            }
        }
    }

    /**
     * 获取用户指定信息
     *
     * @param userID 用户ID
     * @param key 字段
     * @return 指定字段信息
     */
    public String get(String userID, String key){
         return RedisManager.hget(Keys.REDIS_USER_PREFIX + userID, key);
    }
}
