package com.douban.controller;

import com.douban.entity.Movie;
import com.douban.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations() {
        Map<String, Object> result = new HashMap<>();

        // 热门推荐（高分电影）
        List<Movie> topRated = movieService.getTopRated(10);
        result.put("topRated", topRated);

        // 最新上映
        List<Movie> recent = movieService.getRecent(10);
        result.put("recent", recent);

        return ResponseEntity.ok(result);
    }
}
