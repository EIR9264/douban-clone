package com.douban.controller;

import com.douban.dto.PageResult;
import com.douban.entity.MovieCollection;
import com.douban.service.InteractionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    @Autowired
    private InteractionService interactionService;

    @GetMapping
    public ResponseEntity<?> getCollections(
            @RequestParam(defaultValue = "wish") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        PageResult<MovieCollection> result = interactionService.getUserCollections(userId, status, page, size);
        return ResponseEntity.ok(result);
    }
}
