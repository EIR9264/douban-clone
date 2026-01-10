package com.douban.controller;

import com.douban.dto.PageResult;
import com.douban.entity.Review;
import com.douban.mapper.ReviewMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    @GetMapping("/top")
    public ResponseEntity<PageResult<Review>> topReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        int safePage = Math.max(1, page);
        int safeSize = Math.min(50, Math.max(1, size));
        int offset = (safePage - 1) * safeSize;
        Long viewerId = (Long) httpRequest.getAttribute("userId");
        List<Review> items = reviewMapper.findTopLiked(viewerId, safeSize, offset);
        int total = reviewMapper.countAll();
        return ResponseEntity.ok(new PageResult<>(items, safePage, safeSize, total));
    }
}
