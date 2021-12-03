/*
 *
 *  * Copyright (c) 2021 zf1976
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

package com.zf1976.mayi.commom.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.zf1976.mayi.commom.cache.serializer.KryoRedisSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;


/**
 * @author mac
 * Create by Ant on 2020/10/3 14:41
 */
@Configuration
@EnableCaching
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@SuppressWarnings({"rawtypes, unchecked", "DuplicatedCode"})
public class RedisConfiguration extends CachingConfigurerSupport {

    @Bean(name = "kryoRedisTemplate")
    public RedisTemplate<Object, Object> kryoRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        return this.setKryoSerializer(template, factory);
    }

    @Bean(name = "kryoRedisMapTemplate")
    public RedisTemplate<Object, Map<Object, Object>> kryoRedisMapTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Map<Object, Object>> template = new RedisTemplate<>();
        return this.setKryoSerializer(template, factory);
    }


    @Bean(name = "jacksonRedisTemplate")
    public RedisTemplate<Object, Object> jacksonRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer mapJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        return this.setJacksonSerializer(template, factory, mapJackson2JsonRedisSerializer);
    }


    @Bean(name = "jacksonRedisMapTemplate")
    public RedisTemplate<Object, Map<Object, Object>> jacksonRedisMapTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Map<Object, Object>> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer mapJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Map.class);
        return this.setJacksonSerializer(template, factory, mapJackson2JsonRedisSerializer);
    }


    private RedisTemplate setKryoSerializer(RedisTemplate<?,?> template, RedisConnectionFactory factory) {
        final var kryoRedisSerializer = new KryoRedisSerializer();
        final var stringRedisSerializer = new StringRedisSerializer();
        template.setConnectionFactory(factory);
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(kryoRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(kryoRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    private RedisTemplate setJacksonSerializer(RedisTemplate<?,?> template, RedisConnectionFactory factory, Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.activateDefaultTyping(new LaissezFaireSubTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        final var stringRedisSerializer = new StringRedisSerializer();
        template.setConnectionFactory(factory);
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

}

