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
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联字段
    private String username;
    private String avatar;
    private Integer userRating;

    // 当前登录用户是否点过赞（可为空）
    private Boolean liked;

    private String movieTitle;
    private String moviePoster;
    private Integer movieYear;
}
