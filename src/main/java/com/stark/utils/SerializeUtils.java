package com.stark.utils;


import com.stark.Block;

import java.io.*;
import java.util.HashMap;

/**
 * 序列化工具类
 */
public class SerializeUtils {

    /**
     * 反序列化
     */
    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Object result = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream)) {
            result = inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return result;
    }

    /**
     * 对象序列化
     */
    public static byte[] serialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeObject(obj);
        } catch (IOException ignored) {
        }
        return byteArrayOutputStream.toByteArray();
    }
}
