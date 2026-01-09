package com.douban.config;

import com.douban.interceptor.SqlAuditInterceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

    @Bean
    public ConfigurationCustomizer sqlAuditCustomizer(SqlAuditInterceptor sqlAuditInterceptor) {
        return configuration -> configuration.addInterceptor(sqlAuditInterceptor);
    }
}
