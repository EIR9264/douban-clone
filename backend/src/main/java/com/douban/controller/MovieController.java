package com.douban.controller;

import com.douban.dto.*;
import com.douban.entity.Movie;
import com.douban.entity.Review;
import com.douban.service.InteractionService;
import com.douban.service.MovieService;
import com.douban.service.RankingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private InteractionService interactionService;

    @Autowired
    private RankingService rankingService;

    @GetMapping
    public ResponseEntity<PageResult<Movie>> getMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(movieService.getMovies(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovie(@PathVariable Long id) {
        try {
            Movie movie = movieService.getMovieById(id);
            rankingService.recordMovieView(id);
            return ResponseEntity.ok(movie);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/top")
    public ResponseEntity<List<Movie>> getTopRated(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(movieService.getTopRated(limit));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Movie>> getRecent(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(movieService.getRecent(limit));
    }

    @GetMapping("/hot")
    public ResponseEntity<List<Movie>> getHot(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(rankingService.getHotMovies(limit));
    }

    @GetMapping("/most-reviewed")
    public ResponseEntity<List<Movie>> getMostReviewed(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(movieService.getMostReviewed(limit));
    }

    @GetMapping("/most-wished")
    public ResponseEntity<List<Movie>> getMostWished(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(movieService.getMostWished(limit));
    }

    @GetMapping("/most-watched")
    public ResponseEntity<List<Movie>> getMostWatched(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(movieService.getMostWatched(limit));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResult<Movie>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(movieService.search(q, page, size));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<PageResult<Movie>> getByGenre(
            @PathVariable String genre,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(movieService.getByGenre(genre, page, size));
    }

    // 评分
    @PostMapping("/{id}/rating")
    public ResponseEntity<?> rateMovie(
            @PathVariable Long id,
            @Valid @RequestBody RatingRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        return ResponseEntity.ok(interactionService.rateMovie(userId, id, request.getScore()));
    }

    @GetMapping("/{id}/rating")
    public ResponseEntity<?> getUserRating(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(Map.of("rating", (Object) null));
        }
        return ResponseEntity.ok(Map.of("rating", interactionService.getUserRating(userId, id)));
    }

    @DeleteMapping("/{id}/rating")
    public ResponseEntity<?> removeRating(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        interactionService.removeRating(userId, id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 评论
    @GetMapping("/{id}/reviews")
    public ResponseEntity<PageResult<Review>> getReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(interactionService.getMovieReviews(id, page, size));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> createReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        Review review = interactionService.createReview(userId, id, request.getTitle(), request.getContent());
        return ResponseEntity.ok(review);
    }

    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<?> likeReview(@PathVariable Long reviewId) {
        interactionService.likeReview(reviewId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        try {
            interactionService.deleteReview(reviewId, userId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 收藏
    @PostMapping("/{id}/collect")
    public ResponseEntity<?> collectMovie(
            @PathVariable Long id,
            @Valid @RequestBody CollectionRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        return ResponseEntity.ok(interactionService.collectMovie(userId, id, request.getStatus()));
    }

    @DeleteMapping("/{id}/collect")
    public ResponseEntity<?> removeCollection(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        interactionService.removeCollection(userId, id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getMovieStatus(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(Map.of("rating", (Object) null, "collection", (Object) null));
        }
        return ResponseEntity.ok(interactionService.getUserMovieStatus(userId, id));
    }
}
