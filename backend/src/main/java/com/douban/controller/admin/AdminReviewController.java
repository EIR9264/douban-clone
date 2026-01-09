package com.douban.controller.admin;

import com.douban.dto.PageResult;
import com.douban.entity.Review;
import com.douban.mapper.ReviewMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    private final ReviewMapper reviewMapper;

    public AdminReviewController(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    @GetMapping
    public PageResult<Review> list(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(1, page);
        int safeSize = Math.min(50, Math.max(1, size));
        int offset = (safePage - 1) * safeSize;
        List<Review> items = reviewMapper.findAllWithDetails(safeSize, offset);
        int total = reviewMapper.countAll();
        return new PageResult<>(items, safePage, safeSize, total);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        reviewMapper.adminDelete(id);
        return Map.of("message", "评论已删除");
    }
}

