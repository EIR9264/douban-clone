package com.douban.service;

import com.douban.dto.PageResult;
import com.douban.entity.MovieCollection;
import com.douban.entity.Rating;
import com.douban.entity.Review;
import com.douban.mapper.CollectionMapper;
import com.douban.mapper.MovieMapper;
import com.douban.mapper.RatingMapper;
import com.douban.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InteractionService {

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private MovieMapper movieMapper;

    // 评分相关
    @Transactional
    public Rating rateMovie(Long userId, Long movieId, int score) {
        Rating existing = ratingMapper.findByUserAndMovie(userId, movieId);
        Rating rating = new Rating();
        rating.setUserId(userId);
        rating.setMovieId(movieId);
        rating.setScore(score);

        if (existing != null) {
            ratingMapper.update(rating);
        } else {
            ratingMapper.insert(rating);
        }

        // 更新电影平均评分
        updateMovieRating(movieId);

        return ratingMapper.findByUserAndMovie(userId, movieId);
    }

    public Rating getUserRating(Long userId, Long movieId) {
        return ratingMapper.findByUserAndMovie(userId, movieId);
    }

    @Transactional
    public void removeRating(Long userId, Long movieId) {
        ratingMapper.delete(userId, movieId);
        updateMovieRating(movieId);
    }

    private void updateMovieRating(Long movieId) {
        Double avgScore = ratingMapper.getAverageScore(movieId);
        int count = ratingMapper.countByMovie(movieId);
        BigDecimal rating = avgScore != null
                ? BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        movieMapper.updateRating(movieId, rating, count);
    }

    // 评论相关
    public PageResult<Review> getMovieReviews(Long movieId, int page, int size) {
        int offset = (page - 1) * size;
        List<Review> reviews = reviewMapper.findByMovie(movieId, size, offset);
        int total = reviewMapper.countByMovie(movieId);
        return new PageResult<>(reviews, page, size, total);
    }

    public Review createReview(Long userId, Long movieId, String title, String content) {
        Review review = new Review();
        review.setUserId(userId);
        review.setMovieId(movieId);
        review.setTitle(title);
        review.setContent(content);
        reviewMapper.insert(review);
        return reviewMapper.findById(review.getId());
    }

    public void deleteReview(Long reviewId, Long userId) {
        int affected = reviewMapper.delete(reviewId, userId);
        if (affected == 0) {
            throw new RuntimeException("评论不存在或无权删除");
        }
    }

    public void likeReview(Long reviewId) {
        reviewMapper.incrementLikes(reviewId);
    }

    // 收藏相关
    public MovieCollection collectMovie(Long userId, Long movieId, String status) {
        MovieCollection existing = collectionMapper.findByUserAndMovie(userId, movieId);
        if (existing != null) {
            collectionMapper.updateStatus(userId, movieId, status);
        } else {
            MovieCollection collection = new MovieCollection();
            collection.setUserId(userId);
            collection.setMovieId(movieId);
            collection.setStatus(status);
            collectionMapper.insert(collection);
        }
        return collectionMapper.findByUserAndMovie(userId, movieId);
    }

    public void removeCollection(Long userId, Long movieId) {
        collectionMapper.delete(userId, movieId);
    }

    public String getCollectionStatus(Long userId, Long movieId) {
        return collectionMapper.getStatus(userId, movieId);
    }

    public PageResult<MovieCollection> getUserCollections(Long userId, String status, int page, int size) {
        int offset = (page - 1) * size;
        List<MovieCollection> collections = collectionMapper.findByUserAndStatus(userId, status, size, offset);
        int total = collectionMapper.countByUserAndStatus(userId, status);
        return new PageResult<>(collections, page, size, total);
    }

    // 获取用户对某电影的互动状态
    public Map<String, Object> getUserMovieStatus(Long userId, Long movieId) {
        Map<String, Object> status = new HashMap<>();
        Rating rating = ratingMapper.findByUserAndMovie(userId, movieId);
        String collectionStatus = collectionMapper.getStatus(userId, movieId);
        status.put("rating", rating != null ? rating.getScore() : null);
        status.put("collection", collectionStatus);
        return status;
    }
}
