package com.zf1976.mayi.auth.serialize;

//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.json.JsonReadFeature;
//import com.fasterxml.jackson.databind.*;
//import com.zf1976.mayi.common.core.constants.ParameterConstants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.TimeZone;
//
///**
// * @author mac
// * @date 2021/2/11
// **/
//public class JacksonSerializationStrategy extends StandardStringSerializationStrategy {
//
//    protected final static ObjectMapper MAPPER = new ObjectMapper();
//
//    static {
//        MAPPER.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, false);
//        // 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
//        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        // 忽略空Bean转json的错误
//        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        // 允许不带引号的字段名称
//        MAPPER.configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true);
//        // 允许单引号
//        MAPPER.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
//        // allow int startWith 0
//        MAPPER.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true);
//        // 允许字符串存在转义字符：\r \n \t
//        MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
//        // 排除空值字段
//        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        // 使用驼峰式
//        MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
//        // 使用bean名称
//        MAPPER.enable(MapperFeature.USE_STD_BEAN_NAMING);
//        // 忽略空字段
//        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        // 所有日期格式都统一为固定格式
//        MAPPER.setDateFormat(new SimpleDateFormat(ParameterConstants.DATETIME_FORMAT));
//        // 设置时区
//        MAPPER.setTimeZone(TimeZone.getTimeZone(ParameterConstants.TIME_ZONE_GMT8));
//    }
//
//    private final Logger log = LoggerFactory.getLogger("[JacksonSerializationStrategy]");
//
//    @Override
//    protected <T> T deserializeInternal(byte[] bytes, Class<T> aClass) {
//        try {
//            return MAPPER.readValue(bytes, aClass);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e.getCause());
//        }
//        return null;
//    }
//
//    @Override
//    protected byte[] serializeInternal(Object o) {
//        try {
//            return MAPPER.writeValueAsBytes(o);
//        } catch (JsonProcessingException e) {
//            log.error(e.getMessage(), e.getCause());
//        }
//        return new byte[0];
//    }
//}
