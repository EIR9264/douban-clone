package com.douban.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Review {
    private Long id;
    private Long userId;
    private Long movieId;
    private String title;
    private String content;
    private Integer likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联字段
    private String username;
    private String avatar;
    private Integer userRating;
}
