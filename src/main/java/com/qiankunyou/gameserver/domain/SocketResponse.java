package com.qiankunyou.gameserver.domain;

/**
 * User: mengmeng.cheng
 * Date: 4/10/14
 * Time: 11:55 AM
 * Email: chengmengmeng@gmail.com
 */

import java.util.HashMap;
import java.util.Map;

/**
 * 响应数据
 *
 * 回复指令：length(指令长度) + number(指令编号) + sequence(指令序号) + reserve(保留) + result(返回结果) + data(指令体)
 */
public class SocketResponse {

    private int length;

    private int number;

    private int sequence;

    private int reserve;

    private int result;

    private Map<String, String> valueMap = new HashMap<String, String>();

    private String IP;

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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Map<String, String> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, String> valueMap) {
        this.valueMap = valueMap;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setValue(String key, String value){
        this.valueMap.put(key, value);
    }

    public String getValue(String key) {
        return this.valueMap.containsKey(key) ? this.valueMap.get(key) : null;
    }

    @Override
    public String toString() {
        return "SocketResponse{" +
                "length=" + length +
                ", number=" + number +
                ", sequence=" + sequence +
                ", reserve=" + reserve +
                ", result=" + result +
                ", valueMap=" + valueMap +
                ", IP='" + IP + '\'' +
                '}';
    }
}
