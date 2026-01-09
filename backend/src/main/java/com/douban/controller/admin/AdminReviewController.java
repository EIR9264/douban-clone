package com.douban.controller.admin;

import com.douban.mapper.ReviewMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    private final ReviewMapper reviewMapper;

    public AdminReviewController(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        reviewMapper.adminDelete(id);
        return Map.of("message", "评论已删除");
    }
}
