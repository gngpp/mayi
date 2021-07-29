package com.zf1976.mayi.common.datasource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.delete.Delete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis Plus 通用配置
 *
 * @author ant
 * Create by Ant on 2020/10/15 4:43 下午
 */
@Configuration(proxyBeanMethods = false)
public class MybatisAutoConfiguration implements WebMvcConfigurer {

    private final Logger log = LoggerFactory.getLogger("[MybatisAutoConfiguration]");

    /**
     * SQL 过滤器避免SQL 注入
     * @param argumentResolvers 参数解析
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SqlFilterArgumentResolver());
    }

    /**
     * 配置乐观锁插件
     *
     * @return /
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    /**
     * 阻止恶意的全表更新删除
     *
     * @return /
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        List<ISqlParser> sqlParserList = new ArrayList<>();
        // 攻击 SQL 阻断解析器、加入解析链
        sqlParserList.add(new BlockAttackSqlParser() {
            @Override
            public void processDelete(Delete delete) {
                // 如果你想自定义做点什么，可以重写父类方法像这样子
                final Expression where = delete.getWhere();
                if (log.isInfoEnabled()) {
                    log.info("攻击 SQL 阻断解析器拦截到语句：{}", where);
                }
                super.processDelete(delete);
            }
        });
        paginationInterceptor.setSqlParserList(sqlParserList);
        // 单数据库类型设置，避免每次分页抓数据库类型
        paginationInterceptor.setDbType(DbType.MYSQL);
        // 分页限制
        paginationInterceptor.setLimit(1000);
        return paginationInterceptor.setOverflow(false)
                                    .setCountSqlParser(new JsqlParserCountOptimize(true))
                                    .setLimit(-1);

    }

}
