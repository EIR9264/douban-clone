package com.douban.dto;

import lombok.Data;

@Data
public class MovieRequest {
    private String title;
    private String originalTitle;
    private Integer year;
    private String directors;
    private String actors;
    private String genres;
    private String country;
    private String language;
    private Integer duration;
    private String summary;
    private String poster;
    private String images;
}
