package com.qiankunyou.gameserver.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.qiankunyou.gameserver.store.MemoryStore;
import com.qiankunyou.gameserver.store.Store;

/**
 * User: mengmeng.cheng
 * Date: 4/4/14
 * Time: 3:12 PM
 * Email: chengmengmeng@gmail.com
 */
public class SocketManager {
	private static final ScheduledExecutorService scheduledExecutorService = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);

	private static Store store = new MemoryStore();

	public static Store getDefaultStore() {
		return store;
	}

	public static ScheduledFuture<?> schedule(Runnable runnable, long delay) {
        return scheduledExecutorService.schedule(runnable, delay, TimeUnit.SECONDS);
	}
}