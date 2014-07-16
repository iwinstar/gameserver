package com.qiankunyou.gameserver.manager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.log4j.Logger;

/**
 * 调用入口
 *
 * User: mengmeng.cheng
 * Date: 4/4/14
 * Time: 3:16 PM
 * Email: chengmengmeng@gmail.com
 */
public class SocketServer {
	private static final Logger logger = Logger.getLogger(SocketServer.class);

    private final int port;

    public SocketServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childOption(ChannelOption.TCP_NODELAY, true)
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             .childOption(ChannelOption.SO_REUSEADDR, true) //重用地址
             .childOption(ChannelOption.SO_RCVBUF, 1048576)
             .childOption(ChannelOption.SO_SNDBUF, 1048576)
             .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false))  // heap buf 's better
             .childHandler(new ServerChannelInitializer());

            ChannelFuture future = b.bind(port).sync();
            logger.info("Server started on port [" + this.port + "]");
//
//            SocketManager.schedule(new HeartbeatTimer(), Config.HEARTBEAT_INTERVAL);
//            logger.info("Scheduled HeartbeatTimer ...");
//
//            SocketManager.schedule(new LayoutTimer(), Config.LAYOUT_TIMEOVER);
//            logger.info("Scheduled LayoutTimer ...");
//
//            SocketManager.schedule(new AttackTimer(), Config.ATTACK_TIMEOVER);
//            logger.info("Scheduled AttackTimer ...");

            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}