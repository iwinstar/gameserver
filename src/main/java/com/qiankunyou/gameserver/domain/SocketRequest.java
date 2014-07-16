package com.qiankunyou.gameserver.domain;

/**
 * User: mengmeng.cheng
 * Date: 4/10/14
 * Time: 11:45 AM
 * Email: chengmengmeng@gmail.com
 */

import java.util.HashMap;
import java.util.Map;

/**
 * 请求数据
 *
 * 请求指令：length(指令长度) + number(指令编号) + sequence(指令序号) + reserve(保留) + sourceID(源角色ID) + targetID(对手角色ID) + data(指令体)
 */
public class SocketRequest {
    private int length;

    private int number;

    private int sequence;

    private int reserve;

    private int sourceID;

    private int targetID;

    private String IP;

    private Map<String, String> paramMap = new HashMap<String, String>();

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getReserve() {
        return reserve;
    }

    public void setReserve(int reserve) {
        this.reserve = reserve;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public int getTargetID() {
        return targetID;
    }

    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public void setValue(String key, String value) {
        paramMap.put(key, value);
    }

    public String getValue(String key) {
        return paramMap.containsKey(key) ? paramMap.get(key) : null;
    }

    @Override
    public String toString() {
        return "SocketRequest{" +
                "length=" + length +
                ", number=" + number +
                ", sequence=" + sequence +
                ", reserve=" + reserve +
                ", sourceID=" + sourceID +
                ", targetID=" + targetID +
                ", IP='" + IP + '\'' +
                ", paramMap=" + paramMap +
                '}';
    }
}
