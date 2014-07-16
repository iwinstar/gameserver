package com.qiankunyou.gameserver.redis;

import com.qiankunyou.gameserver.common.Keys;

/**
 * User: mengmeng.cheng
 * Date: 4/13/14
 * Time: 4:09 PM
 * Email: chengmengmeng@gmail.com
 */
public class OpenUserRedis {
    private static final OpenUserRedis instance = new OpenUserRedis();

    public static OpenUserRedis getInstance(){
        return instance;
    }

    /**
     * 根据开发平台的用户信息获取本平台的用户ID
     *
     * @param openUserID 开放平台的用户ID
     * @param openSource 开放平台类型
     * @return 用户ID
     */
    public String get(String openUserID, String openSource) {
        return RedisManager.hget(Keys.REDIS_OPEN_USER, openUserID + Keys.DELIMITER + openSource);
    }

    /**
     * 增加开放平台用户与本平台用户的对应关系
     *
     * @param openUserID 开放平台的用户ID
     * @param openSource 开放平台类型
     * @param userID 本平台用户ID
     */
    public void add(String openUserID, String openSource, String userID) {
        RedisManager.hset(Keys.REDIS_OPEN_USER, openUserID + Keys.DELIMITER + openSource, userID);
    }
}
