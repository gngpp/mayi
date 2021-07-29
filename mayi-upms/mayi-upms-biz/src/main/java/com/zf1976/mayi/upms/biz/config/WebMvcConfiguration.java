package com.zf1976.mayi.upms.biz.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.power.common.util.FileUtil;
import com.zf1976.mayi.common.core.config.ThreadPoolProperties;
import com.zf1976.mayi.common.datasource.config.handle.MetaDataHandler;
import com.zf1976.mayi.upms.biz.property.FileProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author mac
 * Create by Ant on 2020/8/30 下午1:58
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer, InitializingBean {

    private final FileProperties fileProperties;
    private final ThreadPoolProperties poolProperties;

    public WebMvcConfiguration(FileProperties fileProperties, ThreadPoolProperties poolProperties) {
        this.fileProperties = fileProperties;
        this.poolProperties = poolProperties;
    }

    /**
     * 配置线程池
     *
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean(name = "bigTaskExecutor")
    public ThreadPoolTaskExecutor bigTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //此方法返回可用处理器的虚拟机的最大数量; 不小于1
        taskExecutor.setBeanName(poolProperties.getBeanName());
        taskExecutor.setThreadGroupName(poolProperties.getThreadGroupName());
        taskExecutor.setCorePoolSize(poolProperties.getCorePoolSize());
        taskExecutor.setMaxPoolSize(poolProperties.getMaxPoolSize());
        taskExecutor.setQueueCapacity(poolProperties.getQueueCapacity());
        taskExecutor.setKeepAliveSeconds(poolProperties.getKeepAliveSeconds());
        taskExecutor.setThreadNamePrefix(poolProperties.getNamePrefix());//线程名称前缀
        // 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy；默认为后者
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }


    /**
     * 允许iframe
     *
     * @return Filter
     */
    private Filter filter() {
        return (servletRequest, servletResponse, filterChain) -> {
            ((HttpServletResponse) servletResponse).addHeader("X-Frame-Options", "ALLOW-FROM");
            filterChain.doFilter(servletRequest, servletResponse);
        };
    }

    @Bean
    @SuppressWarnings("rawtypes")
    public FilterRegistrationBean testFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean<>(filter());
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MetaDataHandler());
        return globalConfig;
    }

    /**
     * 映射静态资源路径
     *
     * @param registry 路径注册
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        final FileProperties.Relative relative = this.fileProperties.getRelative();
        final FileProperties.Real real = this.fileProperties.getReal();
        final String avatarPath = resolveSystemPath(real.getAvatarPath());
        final String filePath = resolveSystemPath(real.getFilePath());
        registry.addResourceHandler("/static/**", relative.getAvatarUrl(), relative.getFileUrl())
                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/", avatarPath, filePath);

    }

    /**
     * 获取完整文件路径
     *
     * @param realPath 配置路径
     * @return /
     */
    private String resolveSystemPath(String realPath) {
        final String homePath = System.getProperty("user.home");
        final File file = new File(homePath + this.fileProperties.getWorkFilePath(), realPath);
        FileUtil.mkdirs(file.getAbsolutePath());
        return ResourceUtils.FILE_URL_PREFIX + file.getAbsolutePath() + AntPathMatcher.DEFAULT_PATH_SEPARATOR;
    }

    @Override
    public void afterPropertiesSet() {
        final String homePath = System.getProperty("user.home");
        final FileProperties.Real real = this.fileProperties.getReal();
        FileProperties.setAvatarRealPath(homePath + this.fileProperties.getWorkFilePath() + real.getAvatarPath());
        FileProperties.setFileRealPath(homePath + this.fileProperties.getWorkFilePath() + real.getFilePath());
    }
}
