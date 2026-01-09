package com.douban.service;

import com.douban.entity.Movie;
import com.douban.mapper.MovieMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RankingService {
    private static final Logger log = LoggerFactory.getLogger(RankingService.class);
    private static final String KEY_MOVIE_HOT = "douban:movie:hot";

    private final StringRedisTemplate redisTemplate;
    private final MovieMapper movieMapper;

    public RankingService(StringRedisTemplate redisTemplate, MovieMapper movieMapper) {
        this.redisTemplate = redisTemplate;
        this.movieMapper = movieMapper;
    }

    public void recordMovieView(Long movieId) {
        if (movieId == null) return;
        try {
            redisTemplate.opsForZSet().incrementScore(KEY_MOVIE_HOT, movieId.toString(), 1d);
        } catch (Exception e) {
            log.debug("Redis unavailable, skip recording movie view: {}", e.getMessage());
        }
    }

    public List<Movie> getHotMovies(int limit) {
        if (limit <= 0) return List.of();

        Map<Long, Long> hotById = new LinkedHashMap<>();
        try {
            Set<ZSetOperations.TypedTuple<String>> tuples =
                    redisTemplate.opsForZSet().reverseRangeWithScores(KEY_MOVIE_HOT, 0, limit - 1);
            if (tuples != null) {
                for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                    if (tuple == null || tuple.getValue() == null) continue;
                    Long id = tryParseLong(tuple.getValue());
                    if (id == null) continue;
                    long score = tuple.getScore() == null ? 0L : Math.round(tuple.getScore());
                    hotById.put(id, score);
                }
            }
        } catch (Exception e) {
            log.debug("Redis unavailable, fall back hot ranking: {}", e.getMessage());
        }

        if (hotById.isEmpty()) {
            // 无 Redis 数据时：退化为“最多看过” + “最多评论” 的综合感觉
            List<Movie> mostWatched = movieMapper.findMostWatched(limit);
            for (Movie m : mostWatched) {
                if (m != null && m.getId() != null) {
                    long watched = m.getWatchedCount() == null ? 0L : m.getWatchedCount();
                    m.setHot(watched);
                }
            }
            return mostWatched;
        }

        List<Long> ids = new ArrayList<>(hotById.keySet());
        List<Movie> movies = movieMapper.findByIdsOrdered(ids);
        for (Movie m : movies) {
            if (m == null || m.getId() == null) continue;
            m.setHot(hotById.getOrDefault(m.getId(), 0L));
        }
        return movies;
    }

    private static Long tryParseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception ignored) {
            return null;
        }
    }
}
