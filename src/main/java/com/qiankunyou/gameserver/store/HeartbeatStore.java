package com.qiankunyou.gameserver.store;

import io.netty.util.Timer;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: mengmeng.cheng
 * Date: 4/15/14
 * Time: 9:20 PM
 * Email: chengmengmeng@gmail.com
 */
public class HeartbeatStore {

    private static final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<String, Timer>();

    public static void remove(String sessionId) {
        timers.remove(sessionId);
    }

    public static void add(String sessionId, Timer timer) {
        timers.put(sessionId, timer);
    }

    public static Collection<Timer> getClients() {
        return timers.values();
    }

    public static Timer get(String sessionId) {
        return timers.get(sessionId);
    }

    public static boolean checkExist(String sessionId) {
        return timers.containsKey(sessionId);
    }
}
