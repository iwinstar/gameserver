package com.qiankunyou.gameserver.redis;

import com.qiankunyou.gameserver.common.Config;
import com.qiankunyou.gameserver.common.Keys;
import redis.clients.jedis.Tuple;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: mengmeng.cheng
 * Date: 4/16/14
 * Time: 11:37 AM
 * Email: chengmengmeng@gmail.com
 */
public class RankRedis {
    private static final RankRedis instance = new RankRedis();

    public static RankRedis getInstance() {
        return instance;
    }

    /**
     * 增加排行榜数据
     *
     * @param score 积分
     * @param userID 用户ID
     */
    public void add(int score, String userID)   {
        RedisManager.zadd(Keys.REDIS_PLAIN_GAME_RANK, score, userID);
    }

    /**
     * 增加排行榜数据
     *
     * @param userID 用户ID
     * @param score 积分
     */
    public void add(String userID, int score)   {
        RedisManager.zincrBy(Keys.REDIS_PLAIN_GAME_RANK, userID, score);
    }

    /**
     * 获取用户积分信息
     *
     * @param userID 用户ID
     * @return 用户积分
     */
    public int getScore(String userID) {
        return (int) RedisManager.zscore(Keys.REDIS_PLAIN_GAME_RANK, userID);
    }

    /**
     * 获取用户积分倒序排名
     *
     * @param userID 用户ID
     * @return 积分倒序排名
     */
    public int getRank(String userID) {
        return (int) RedisManager.zrevrank(Keys.REDIS_PLAIN_GAME_RANK, userID);
    }

    /**
     * 获取排行段
     *
     * @param start 排名起始
     * @param stop 排名结束
     * @return 排名信息
     */
    public Set<Tuple> getRangeWithScores(int start, int stop) {
        return RedisManager.zrevrangeWithScores(Keys.REDIS_PLAIN_GAME_RANK, start, stop);
    }

    /**
     * 获取排行榜
     *
     * @param userID 用户ID
     * @return 排行榜Map
     */
    public Map<String, String> getRanking(String userID){
        boolean flag = true;
        int start = 1;
        Set<Tuple> set = RankRedis.getInstance().getRangeWithScores(start - 1, Config.RANKING_MAX - 1);

        Map<String, String> map = new LinkedHashMap<String, String>();

        for (Tuple tuple : set) {
            String rankUserID = tuple.getElement();
            long score = (long)tuple.getScore();
            String name = UserRedis.getInstance().get(userID, Keys.USER_NAME);
            map.put(String.valueOf(start ++), rankUserID + Keys.DELIMITER + name + Keys.DELIMITER + score);

            if (rankUserID.equals(userID)) flag = false;
        }

        if (flag) {
            long rank = RankRedis.getInstance().getRank(userID);
            long score = RankRedis.getInstance().getScore(userID);
            String name = UserRedis.getInstance().get(userID, Keys.USER_NAME);

            map.put(String.valueOf(rank), userID + Keys.DELIMITER + name + Keys.DELIMITER + score);
        }

        return map;
    }
}
