package com.qiankunyou.gameserver.store;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定义RedisStore
 *
 * User: mengmeng.cheng
 * Date: 4/4/14
 * Time: 3:09 PM
 * Email: chengmengmeng@gmail.com
 */
public class MemoryStore implements Store {

	private static final ConcurrentHashMap<String, ChannelHandlerContext> clients = new ConcurrentHashMap<String, ChannelHandlerContext>();

	@Override
	public ChannelHandlerContext get(String key) {
        if (this.checkExist(key)) {
            return clients.get(key);
        }

		return null;
	}

	@Override
	public void remove(String key) {
        clients.remove(key);
	}

	@Override
	public void add(String key, ChannelHandlerContext client) {
		if (key == null || client == null) {
            return;
        }

        clients.put(key, client);
	}

	@Override
	public Collection<ChannelHandlerContext> getClients() {
        return clients.values();
	}

	@Override
	public boolean checkExist(String key) {
        return clients.containsKey(key);
	}

}