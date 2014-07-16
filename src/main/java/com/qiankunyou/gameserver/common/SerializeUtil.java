package com.qiankunyou.gameserver.common;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * User: mengmeng.cheng
 * Date: 4/29/14
 * Time: 10:16 AM
 * Email: chengmengmeng@gmail.com
 */
public class SerializeUtil {
    private static final Logger logger = Logger.getLogger(SerializeUtil.class);

    /**
     * 序列化对象
     *
     * @param object 对象
     * @return byte[]
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos;
        ByteArrayOutputStream baos;

        try {
            //序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.error("serialize object error: " + object + ", message: " + e);
        }

        return null;
    }

    /**
     * 反序列化对象
     *
     * @param bytes byte数组
     * @return object
     */
    public static Object unSerialize(byte[] bytes) {
        ByteArrayInputStream bais;

        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            logger.error("unSerialize bytes error: " + bytes.toString() + ", message: " + e);
        }

        return null;
    }
}
