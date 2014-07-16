package com.qiankunyou.gameserver.common;

/**
 * User: mengmeng.cheng
 * Date: 4/13/14
 * Time: 2:30 PM
 * Email: chengmengmeng@gmail.com
 */
public class Keys {

    // 统一使用的字段分隔符
    public static final String DELIMITER = "_";

    // Redis 用户相关字段
    public static final String REDIS_USER_PREFIX = "user_";

    public static final String USER_ID = "user_id";

    public static final String USER_NAME = "user_name";

    public static final String USER_AVATAR = "avatar";

    public static final String USER_SCORE = "score";

    public static final String USER_DEGREE = "degree";

    public static final String USER_ROOM_ID = "room_id";

    public static final String USER_OPEN_USER_ID = "open_user_id";

    public static final String USER_OPEN_SOURCE = "open_source";

    // Redis 序列相关字段
    public static final String REDIS_SEQUENCE = "sequence";

    public static final String SEQUENCE_USER_ID = "user_id";

    public static final String SEQUENCE_ROOM_ID = "room_id";

    // Redis 开放平台用户对应关系相关字段
    public static final String REDIS_OPEN_USER = "open_user";

    // 战斗结果相关字段
    public static final String BATTLE_RESULT = "result";

    public static final String BATTLE_RESULT_WIN = "0";

    public static final String BATTLE_RESULT_FAIL = "1";

    public static final String BATTLE_RESULT_DRAW = "2";

    // Redis 排行榜相关字段
    public static final String REDIS_PLAIN_GAME_RANK = "plain_game_rank";

    // Redis 游戏房间相关字段
    public static final String REDIS_PLAIN_GAME_ROOM_PREFIX = "plain_game_room_";

    public static final String ROOM_ROUND = "round";

    public static final String ROOM_SOURCE_ID = "source_id";

    public static final String ROOM_TARGET_ID = "target_id";

    public static final String ROOM_SOURCE_DEGREE = "source_degree";

    public static final String ROOM_TARGET_DEGREE = "target_degree";

    public static final String ROOM_LAYOUT_INFO_PREFIX = "layout_info_";

    public static final String ROOM_ATTACK_INFO_PREFIX = "attack_info_";

    public static final String ROOM_ATTACK_RESULT_PREFIX = "attack_result_";

    public static final String ROOM_ATTACK_RESULT_PLAIN_REMAIN = "plain_remain";

    public static final String ROOM_ATTACK_RESULT_AI_PLAIN_REMAIN = "ai_plain_remain";

    public static final String ROOM_RESULT_FUEL_TOTAL = "fuel_total";

    public static final String ROOM_RESULT_NOT_INJURED_TOTAL = "not_injured_total";

    // 等待对手的roomID有序集合
    public static final String REDIS_PLAIN_GAME_WAITING_ROOM = "plain_game_waiting_room";

    public static final String SYSTEM_STATUS = "status";

    public static final String SYSTEM_STATUS_OK = "ok";

    public static final String HEARTBEAT_INTERVAL = "heartbeat_interval";
}
