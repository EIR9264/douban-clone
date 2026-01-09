package com.douban.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Movie {
    private Long id;
    private String title;
    private String originalTitle;
    private Integer year;
    private String directors;
    private String actors;
    private String genres;
    private String country;
    private String language;
    private Integer duration;
    private BigDecimal rating;
    private Integer ratingCount;
    private String summary;
    private String poster;
    private String images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 运行时统计字段（排行榜等场景使用，不落库）
    private Long hot;
    private Integer reviewCount;
    private Integer wishCount;
    private Integer watchedCount;
}
