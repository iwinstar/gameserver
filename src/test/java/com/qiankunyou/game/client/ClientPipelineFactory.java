package com.qiankunyou.game.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 * User: mengmeng.cheng
 * Date: 4/10/14
 * Time: 5:25 PM
 * Email: chengmengmeng@gmail.com
 */
public class ClientPipelineFactory extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

//        pipeline.addLast("decoder", new ClientDecoder());
//        pipeline.addLast("encoder", new ClientEncoder());

        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("handler", new ClientHandler());
    }
}
