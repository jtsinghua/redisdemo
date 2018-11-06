package com.freetsinghua.redisdemo2.util;

import com.freetsinghua.redisdemo2.model.Message;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 针对message类的实现
 * @author z.tsinghua
 * @date 2018/10/30
 */
public class TsRedisSerializer<T> implements RedisSerializer {
    @Override
    public byte[] serialize(Object o) throws SerializationException {

        if (o instanceof Message){
            Message message = (Message) o;
        }

        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return null;
    }
}
