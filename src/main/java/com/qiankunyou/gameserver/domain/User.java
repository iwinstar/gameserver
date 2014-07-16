package com.qiankunyou.gameserver.domain;

import com.qiankunyou.gameserver.common.Keys;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mengmeng.cheng
 * Date: 4/15/14
 * Time: 1:20 PM
 * Email: chengmengmeng@gmail.com
 */
public class User {

    private long userID;

    private String userName;

    private String avatar;

    private int degree;

    private int score;

    private String openUserID;

    private String openSource;

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getOpenUserID() {
        return openUserID;
    }

    public void setOpenUserID(String openUserID) {
        this.openUserID = openUserID;
    }

    public String getOpenSource() {
        return openSource;
    }

    public void setOpenSource(String openSource) {
        this.openSource = openSource;
    }

    public static User fromMap(Map<String, String> map) {
        User user = new User();

        if (map.containsKey(Keys.USER_ID)) user.setUserID(Long.valueOf(map.get(Keys.USER_ID)));
        if (map.containsKey(Keys.USER_NAME)) user.setUserName(map.get(Keys.USER_NAME));
        if (map.containsKey(Keys.USER_AVATAR)) user.setAvatar(map.get(Keys.USER_AVATAR));
        if (map.containsKey(Keys.USER_DEGREE)) user.setDegree(Integer.parseInt(map.get(Keys.USER_DEGREE)));
        if (map.containsKey(Keys.USER_SCORE)) user.setScore(Integer.parseInt(map.get(Keys.USER_SCORE)));
        if (map.containsKey(Keys.USER_OPEN_USER_ID)) user.setOpenUserID(map.get(Keys.USER_OPEN_USER_ID));
        if (map.containsKey(Keys.USER_OPEN_SOURCE)) user.setOpenSource(map.get(Keys.USER_OPEN_SOURCE));

        return user;
    }

    public static Map<String, String> toMap(User user) {
        Map<String, String> map = new HashMap<String, String>();

        map.put(Keys.USER_ID, String.valueOf(user.getUserID()));
        map.put(Keys.USER_NAME, user.getUserName());
        map.put(Keys.USER_AVATAR, user.getAvatar());
        map.put(Keys.USER_DEGREE, String.valueOf(user.getDegree()));
        map.put(Keys.USER_SCORE, String.valueOf(user.getScore()));
        map.put(Keys.USER_OPEN_USER_ID, user.getOpenUserID());
        map.put(Keys.USER_OPEN_SOURCE, user.getOpenSource());

        return map;
    }
}
