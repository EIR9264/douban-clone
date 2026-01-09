package com.douban.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SqlAuditRepository {

    private final JdbcTemplate jdbcTemplate;

    public SqlAuditRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Long userId, String clientIp, String sqlText, boolean blocked) {
        jdbcTemplate.update(
                "INSERT INTO sql_audit (user_id, client_ip, sql_text, blocked) VALUES (?, ?, ?, ?)",
                userId, clientIp, sqlText, blocked ? 1 : 0
        );
    }
}
