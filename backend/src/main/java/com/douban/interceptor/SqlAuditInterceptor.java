package com.douban.interceptor;

import com.douban.repository.SqlAuditRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * MyBatis SQL 审计与简单注入拦截。
 */
@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SqlAuditInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(SqlAuditInterceptor.class);
    private static final Pattern RISKY_SQL_PATTERN = Pattern.compile(
            "(?i)(\\bunion\\b|\\bdrop\\b|\\binsert\\b.+\\binto\\b.+information_schema|sleep\\s*\\(|benchmark\\s*\\(|or\\s+1=1|--|;--|/\\*|\\*/|xp_)");

    private final SqlAuditRepository sqlAuditRepository;

    public SqlAuditInterceptor(SqlAuditRepository sqlAuditRepository) {
        this.sqlAuditRepository = sqlAuditRepository;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String rawSql = boundSql.getSql();
        String normalizedSql = rawSql.replaceAll("\\s+", " ").toLowerCase();

        boolean risky = RISKY_SQL_PATTERN.matcher(normalizedSql).find();

        Long userId = null;
        String clientIp = null;

        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            HttpServletRequest request = servletAttrs.getRequest();
            Object userIdAttr = request.getAttribute("userId");
            if (userIdAttr instanceof Long) {
                userId = (Long) userIdAttr;
            }
            clientIp = request.getRemoteAddr();
        }

        try {
            sqlAuditRepository.insert(userId, clientIp, rawSql, risky);
        } catch (Exception e) {
            log.warn("Failed to persist SQL audit log", e);
        }

        if (risky) {
            log.warn("Blocked risky SQL: {}", normalizedSql);
            throw new RuntimeException("检测到异常SQL，已拦截");
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }
}
