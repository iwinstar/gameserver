package com.qiankunyou.gameserver.common;

/**
 * User: mengmeng.cheng
 * Date: 4/13/14
 * Time: 6:44 PM
 * Email: chengmengmeng@gmail.com
 */
public class StringUtil {

    /**
     * 根据积分计算等级
     *
     * @param score 积分
     * @return 等级
     */
    public static int getDegree(int score) {
        String[] degreeList = Config.DEGREE_POINTS.split(",");
        int i = 0;

        for (; i < degreeList.length; i++) {
            if (score < Integer.valueOf(degreeList[i])) {
                break;
            }
        }

        return i;
    }
}
