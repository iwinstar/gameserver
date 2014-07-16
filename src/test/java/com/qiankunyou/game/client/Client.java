package com.qiankunyou.game.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.apache.log4j.Logger;

/**
 * User: mengmeng.cheng
 * Date: 4/10/14
 * Time: 5:21 PM
 * Email: chengmengmeng@gmail.com
 */
public class Client {
    public static final int port = 9000;
    public static final String host ="127.0.0.1";
    private static final Logger logger = Logger.getLogger(Client.class);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new Client().run();
    }

    /**
     * 启动客户端
     * @return
     * @throws Exception
     */
    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientPipelineFactory());

            // Make a new connection.
            ChannelFuture f = b.connect(host, port).sync();

            // Get the handler instance to retrieve the answer.
            f.channel().pipeline().last();
        } finally {
            group.shutdownGracefully();
        }
    }
}
