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

package com.zf1976.mayi.upms.biz.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * @date 2021/1/20
 **/
@Component
public class PageUtil {

    @Autowired
    private RedisTemplate<Object, Object> kryoRedisTemplate;
    /**
     * 存放单个hash缓存
     * @param key 键
     * @param hKey 键
     * @param value 值
     * @return /
     */
    public boolean hPut(String key, String hKey, Object value) {
        try {
            kryoRedisTemplate.opsForHash().put(key, hKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 分页存取数据
     * @param key  hash存取的key
     * @param hKey hash存取的hKey
     * @param score 指定字段排序
     * @param value value
     * @return /
     */
    public Boolean setPage(String key, String hKey, double score, String value){
        Boolean result = false;
        try {
            result = kryoRedisTemplate.opsForZSet().add(key + ":page", hKey, score);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置辅助分页的过期时间
        kryoRedisTemplate.expire(key+":page",1800000 , TimeUnit.MILLISECONDS);
        return result;
    }

    /**
     * 分页取出 hash中hKey值
     *
     * @param key key
     * @param offset 位置
     * @param count 总数
     * @return /
     */
    public Set<Object> getPage(String key, int offset, int count){
        Set<Object> result = null;
        try {
            result = kryoRedisTemplate.opsForZSet().rangeByScore(key+":page", 1, 100000, (long) (offset - 1) *count, count);
            //1 100000代表score的排序氛围值，即从1-100000的范围
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 计算key值对应的数量
     *
     * @param key key
     * @return /
     */
    public Integer getSize(String key){
        Integer num = 0;
        try {
            Long size = kryoRedisTemplate.opsForZSet().zCard(key+":page");
            assert size != null;
            return size.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }
}
