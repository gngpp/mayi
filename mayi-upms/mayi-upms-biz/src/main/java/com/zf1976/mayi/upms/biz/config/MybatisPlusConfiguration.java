package com.zf1976.mayi.upms.biz.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zf1976.mayi.common.datasource.config.handle.MetaDataHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mac
 * @date 2021/4/10
 */
@Configuration
public class MybatisPlusConfiguration {

    /**
     * 自动填充拦截
     *
     * @date 2021-04-10 10:48:14
     * @return {@link MetaObjectHandler}
     */
    @Bean
    public MetaObjectHandler metaObjectHandler(){
        return new MetaDataHandler();
    }
}
