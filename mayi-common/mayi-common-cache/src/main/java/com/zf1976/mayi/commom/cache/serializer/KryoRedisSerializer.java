package com.zf1976.mayi.commom.cache.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author mac
 * 2021/12/3 星期五 11:38 PM
 */
public class KryoRedisSerializer implements RedisSerializer<Object> {

    private final int bufSize;
    //每个线程的 Kryo 实例
    private static final ThreadLocal<Kryo> kryoLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        // no mandatory registration
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    public KryoRedisSerializer() {
        this(1024);
    }

    public KryoRedisSerializer(int bufSize) {
        this.bufSize = bufSize;
    }

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.bufSize);
        Output output = new Output(byteArrayOutputStream);
        Kryo kryo = getInstance();
        kryo.writeClassAndObject(output, o);
        output.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        Kryo kryo = getInstance();
        return kryo.readClassAndObject(input);
    }

    private final Kryo getInstance() {
        return kryoLocal.get();
    }

}
