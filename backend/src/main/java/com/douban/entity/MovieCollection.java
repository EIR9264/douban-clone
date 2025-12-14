package com.douban.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MovieCollection {
    private Long id;
    private Long userId;
    private Long movieId;
    private String status; // wish, watching, watched
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联字段
    private Movie movie;
}
