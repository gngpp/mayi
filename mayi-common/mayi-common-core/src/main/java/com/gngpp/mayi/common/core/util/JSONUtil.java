/*
 *
 *  * Copyright (c) 2021 gngpp
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

package com.gngpp.mayi.common.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gngpp.mayi.common.core.constants.DateFormatterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author ant
 * Create by Ant on 2021/3/24 2:51 PM
 */
@SuppressWarnings("all")
public class JSONUtil {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    public static final Logger log = LoggerFactory.getLogger("JSONUtil");

    static {
        // 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略空Bean转json的错误
        JSON_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 允许不带引号的字段名称
        JSON_MAPPER.configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true);
        // 允许单引号
        JSON_MAPPER.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
        // allow int startWith 0
        JSON_MAPPER.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true);
        // 允许字符串存在转义字符：\r \n \t
        JSON_MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        // 排除空值字段
        JSON_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 使用驼峰式
        JSON_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        // 使用bean名称
        JSON_MAPPER.enable(MapperFeature.USE_STD_BEAN_NAMING);
        // 忽略空字段
        JSON_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 所有日期格式都统一为固定格式
        JSON_MAPPER.setDateFormat(new SimpleDateFormat(DateFormatterConstants.DATETIME_FORMAT));
        // 设置时区
        JSON_MAPPER.setTimeZone(TimeZone.getTimeZone(DateFormatterConstants.TIME_ZONE_GMT8));
        JSON_MAPPER.registerModule(new JavaTimeModule());
    }


    /**
     * 对象转换为json字符串
     * @param o 要转换的对象
     */
    public static String toJsonString(Object o) {
        return toJsonString(o, false);
    }

    /**
     * 对象转换为json字符串
     * @param o 要转换的对象
     * @param format 是否格式化json
     */
    public static String toJsonString(Object o, boolean format) {
        try {
            if (o == null) {
                return "";
            }
            if (o instanceof Number) {
                return o.toString();
            }
            if (o instanceof String) {
                return (String)o;
            }
            if (format) {
                return JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            }
            return JSON_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转换为指定对象
     * @param json json字符串
     * @param cls 目标对象
     */
    public static <T> T toObject(String json, Class<T> cls) {
        if(org.springframework.util.StringUtils.isEmpty(json) || cls == null){
            return null;
        }
        try {
            return JSON_MAPPER.readValue(json, cls);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转换为指定对象，并增加泛型转义
     * 例如：List<Integer> test = toObject(jsonStr, List.class, Integer.class);
     * @param json json字符串
     * @param parametrized 目标对象
     * @param parameterClasses 泛型对象
     */
    public static <T> T toObject(String json, Class<?> parametrized, Class<?>... parameterClasses) {
        if(org.springframework.util.StringUtils.isEmpty(json) || parametrized == null){
            return null;
        }
        try {
            JavaType javaType = JSON_MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return JSON_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转换为指定对象
     * @param json json字符串
     * @param typeReference 目标对象类型
     */
    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        if(org.springframework.util.StringUtils.isEmpty(json) || typeReference == null){
            return null;
        }
        try {
            return JSON_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转换为JsonNode对象
     * @param jsonObject json Object
     */
    public static JsonNode parse(Object jsonObject){
        return parse(jsonObject.toString());
    }

    /**
     * 字符串转换为JsonNode对象
     * @param json json字符串
     */
    public static JsonNode parse(String json) {
        if (org.springframework.util.StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return JSON_MAPPER.readTree(json);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 对象转换为map对象
     * @param o 要转换的对象
     */
    public static Map<?, ?> toMap(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            return toObject((String)o, Map.class);
        }
        return JSON_MAPPER.convertValue(o, Map.class);
    }

    /**
     * json字符串转换为list对象
     * @param json json字符串
     */
    public static List<?> toList(String json) {
        if (org.springframework.util.StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return JSON_MAPPER.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * json字符串转换为list对象，并指定元素类型
     * @param json json字符串
     * @param cls list的元素类型
     */
    public static <T> List<T> toList(String json, Class<T> cls) throws IOException {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JavaType javaType = JSON_MAPPER.getTypeFactory().constructParametricType(List.class, cls);
            return JSON_MAPPER.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static ObjectMapper getJsonMapper(){
        return JSON_MAPPER;
    }

    public static void writeValue(HttpServletResponse response, Object o){
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        try {
            JSON_MAPPER.writeValue(response.getOutputStream(), o);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static <T> T readValue(String json, Class<T> tClass) {
        try {
            return JSON_MAPPER.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
