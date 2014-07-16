package com.qiankunyou.gameserver.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mengmeng.cheng
 * Date: 4/10/14
 * Time: 1:08 PM
 * Email: chengmengmeng@gmail.com
 */
public class ProtocolUtil {
    /**
     * 编码报文的数据部分
     * @param valueMap valueMap
     * @return ChannelBuffer
     */
    public static ByteBuf encode(Map<String, String> valueMap) {
        ByteBuf totalBuffer = null;

        if (valueMap != null && !valueMap.isEmpty()) {
            totalBuffer = Unpooled.buffer();

            for(String key : valueMap.keySet()) {
                String value = valueMap.get(key);

                ByteBuf buffer = Unpooled.buffer();

                buffer.writeInt(key.length());
                buffer.writeBytes(key.getBytes(CharsetUtil.UTF_8));
                buffer.writeInt(value.length());
                buffer.writeBytes(value.getBytes(CharsetUtil.UTF_8));

                totalBuffer.writeBytes(buffer);
            }
        }

        return totalBuffer;
    }

    /**
     * 解码报文的数据部分
     * @param buffer buffer
     * @return Map
     */
    public static Map<String, String> decode(ByteBuf buffer){
        Map<String, String> dataMap = new HashMap<String, String>();

        if (buffer != null && buffer.readableBytes() > 0) {
            int index = 0;
            int length = buffer.readableBytes();

            while(index < length){

                // 获取Key
                int size = buffer.readInt();

                byte[] contents = new byte[size];
                buffer.readBytes(contents);
                String key = new String(contents, CharsetUtil.UTF_8);
                index += size + 4;

                // 获取Value
                size = buffer.readInt();
                contents = new byte[size];
                buffer.readBytes(contents);
                String value = new String(contents, CharsetUtil.UTF_8);
                index += size + 4;

                dataMap.put(key, value);
            }
        }

        return dataMap;
    }

    /**
     * 获取客户端IP
     * @param channel channel
     * @return String
     */
    public static String getClientIP(Channel channel){
        SocketAddress address = channel.remoteAddress();
        String ip = "";

        if (address != null) {
            ip = address.toString().trim();
            int index = ip.lastIndexOf(':');

            if (index < 1) {
                index = ip.length();
            }
            ip = ip.substring(1, index);
        }

        if (ip.length() > 15) {
            ip = ip.substring(Math.max(ip.indexOf("/") + 1, ip.length() - 15));
        }

        return ip;
    }
}
