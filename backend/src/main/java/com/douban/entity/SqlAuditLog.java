package com.douban.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SqlAuditLog {
    private Long id;
    private Long userId;
    private String clientIp;
    private String sqlText;
    private Boolean blocked;
    private LocalDateTime createdAt;
}
