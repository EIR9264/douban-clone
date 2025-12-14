package com.douban.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Rating {
    private Long id;
    private Long userId;
    private Long movieId;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联字段
    private String username;
    private String avatar;
}
