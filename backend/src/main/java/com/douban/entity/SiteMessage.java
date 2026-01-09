package com.douban.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteMessage {
    private Long id;
    private Long receiverId;
    private Long senderId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
}
