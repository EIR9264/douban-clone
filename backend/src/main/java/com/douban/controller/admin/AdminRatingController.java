package com.douban.controller.admin;

import com.douban.dto.PageResult;
import com.douban.entity.Rating;
import com.douban.mapper.MovieMapper;
import com.douban.mapper.RatingMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/ratings")
public class AdminRatingController {

    private final RatingMapper ratingMapper;
    private final MovieMapper movieMapper;

    public AdminRatingController(RatingMapper ratingMapper, MovieMapper movieMapper) {
        this.ratingMapper = ratingMapper;
        this.movieMapper = movieMapper;
    }

    @GetMapping
    public PageResult<Rating> list(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(1, page);
        int safeSize = Math.min(50, Math.max(1, size));
        int offset = (safePage - 1) * safeSize;
        List<Rating> items = ratingMapper.findAllWithDetails(safeSize, offset);
        int total = ratingMapper.countAll();
        return new PageResult<>(items, safePage, safeSize, total);
    }

    @PutMapping("/{id}")
    public Map<String, String> updateScore(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Rating existing = ratingMapper.findById(id);
        if (existing == null) {
            return Map.of("error", "rating not found");
        }
        Object scoreObj = body.get("score");
        int score = scoreObj instanceof Number ? ((Number) scoreObj).intValue() : 0;
        if (score < 1 || score > 10) {
            return Map.of("error", "score must be 1-10");
        }
        ratingMapper.adminUpdateScore(id, score);
        recalcMovieRating(existing.getMovieId());
        return Map.of("message", "评分已更新");
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        Rating existing = ratingMapper.findById(id);
        if (existing == null) {
            return Map.of("error", "rating not found");
        }
        ratingMapper.adminDelete(id);
        recalcMovieRating(existing.getMovieId());
        return Map.of("message", "评分已删除");
    }

    private void recalcMovieRating(Long movieId) {
        if (movieId == null) return;
        Double avgScore = ratingMapper.getAverageScore(movieId);
        int count = ratingMapper.countByMovie(movieId);
        BigDecimal rating = avgScore != null
                ? BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        movieMapper.updateRating(movieId, rating, count);
    }
}
