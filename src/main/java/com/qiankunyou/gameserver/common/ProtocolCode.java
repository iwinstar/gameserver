package com.qiankunyou.gameserver.common;

/**
 * User: mengmeng.cheng
 * Date: 4/14/14
 * Time: 10:19 PM
 * Email: chengmengmeng@gmail.com
 */
public class ProtocolCode {

    public static final int USER_MIN = 2000;

    public static final int USER_GET_INFO = 2000;  // 指令体为人人userID+来源ID+头像编号+名字

    public static final int USER_SEND_INFO = 2001;  // 服务器回复用户信息指令，编号2001，指令体为唯一用户标识 + 用户数据

    public static final int USER_CHANGE_INFO = 2002; // 指令体为唯一用户标识+头像数值+用户名

    public static final int USER_SEND_CHANGE_INFO = 2003;  //返回码代表处理成功或者失败

    public static final int USER_GET_RANKING = 2004;  // 获取排行榜数据

    public static final int USER_SEND_RANKING = 2005; // 服务器端返回排行榜数据

    public static final int USER_MAX = 2999;

    public static final int BATTLE_MIN = 3000;

    public static final int BATTLE_GET_PLAYER = 3000; // 获取对手

    public static final int BATTLE_SEND_PLAYER = 3001; //  指令体为对手ID和对手的等级，接到此消息进入布局，若为AI，对手ID为0

    public static final int BATTLE_SEND_LAYOUT_TIME_OVER = 3003; // 布局时间到，客户端开始上传布局信息

    public static final int BATTLE_UPLOAD_LAYOUT_INFO = 3004; // 双方客户端上传布局信息

    public static final int BATTLE_SEND_LAYOUT_INFO = 3005; // 服务端分发布局信息

    public static final int BATTLE_SEND_ATTACK_START = 3007; // 开始攻击

    public static final int BATTLE_SEND_ATTACK_TIME_OVER = 3009; // 攻击时间到

    public static final int BATTLE_UPLOAD_ATTACK_INFO = 3010; // 双方客户端上传攻击信息

    public static final int BATTLE_SEND_ATTACK_INFO = 3011; // 服务器端分发攻击信息

    public static final int BATTLE_UPLOAD_ATTACK_RESULT = 3012; // 客户端上传攻击结果 发给服务器对方的 被击杀的飞机油耗总和， 所击杀的飞机未被击伤部分的总和，剩余飞机数

    public static final int BATTLE_SEND_RESULT = 3013; // 服务端下发游戏结果，指令体为成败结果+得分(编号)

    public static final int BATTLE_SEND_PLAYER_OFFLINE = 3015; // 对手掉线，启用AI

    public static final int BATTLE_MAX = 3999;

    public static final int AI_BATTLE_MIN = 4000;

    public static final int AI_BATTLE_SEND_LAYOUT_TIME_OVER = 4003; // AI布局时间到

    public static final int AI_BATTLE_SEND_ATTACK_START = 4007; // 开始攻击

    public static final int AI_BATTLE_SEND_ATTACK_TIME_OVER = 4009; // 攻击时间到

    public static final int AI_BATTLE_UPLOAD_ATTACK_RESULT = 4012; // 上传双方的 被击杀的飞机油耗总和， 所击杀的飞机未被击伤部分的总和，剩余飞机数

    public static final int AI_BATTLE_SEND_RESULT = 4013; // 服务器下发游戏结果

    public static final int AI_BATTLE_MAX = 4999;

    public static final int SYSTEM_MIN = 9000;

    public static final int SYSTEM_GET_INFO = 9000;  // 获取系统配置信息

    public static final int SYSTEM_SEND_INFO = 9001;  // 返回系统配置信息，指令体为心跳数据

    public static final int SYSTEM_UPLOAD_HEARTBEAT = 9002; // 发送心跳，无指令体

    public static final int SYSTEM_SEND_HEARTBEAT = 9003; // 服务器端响应心跳

    public static final int SYSTEM_RELOAD_CONFIG = 9998; // 申请热加载配置

    public static final int SYSTEM_SEND_RELOAD_CONFIG = 9999; // 回复热加载配置

    public static final int SYSTEM_MAX = 9999;
}
