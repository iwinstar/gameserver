package com.qiankunyou.gameserver.store;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;

/**
 * User: mengmeng.cheng
 * Date: 4/4/14
 * Time: 2:29 PM
 * Email: chengmengmeng@gmail.com
 */
public interface Store {

	void remove(String sessionId);

	void add(String sessionId, ChannelHandlerContext ctx);

	Collection<ChannelHandlerContext> getClients();

    ChannelHandlerContext get(String sessionId);

	boolean checkExist(String sessionId);
}
