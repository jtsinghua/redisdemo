package com.freetsinghua.redisdemo2.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.freetsinghua.redisdemo2.model.Message;
import org.springframework.boot.json.JsonParseException;

import java.util.UUID;

/**
 * @author z.tsinghua
 * @date 2018/10/29
 */
public class StringUtils {

    private static final StringBuilder CREATOR = new StringBuilder();

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static boolean isEmpty(final String str) {
        return null == str || str.isEmpty();
    }

    public static Message stringToMessage(String data) {

        try {

            JSONObject obj = JSON.parseObject(data);
            String id = obj.getString("id");
            String src = obj.getString("src");
            String dest = obj.getString("dest");
            String msg = obj.getString("msg");
            String remark = obj.getString("remark");

            return new Message(id, src, dest, msg, remark);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    /**
     * 获取类的短名
     *
     * @param clazz 类实例
     * @return 返回类的短名
     */
    public static String simpleClassName(Class clazz) {
        return clazz.getSimpleName();
    }

    /**
     * 构造redis的key
     *
     * @param tag 标志字段
     * @return key
     */
    public static synchronized String generateKey(final String tag) {
        CREATOR.delete(0, CREATOR.toString().length());
        CREATOR.append(tag)
                .append("#")
                .append(uuid())
                .append("#")
                .append(System.currentTimeMillis());

        return CREATOR.toString();
    }
}
