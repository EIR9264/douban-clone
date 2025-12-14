package com.douban.controller;

import com.douban.dto.UserDTO;
import com.douban.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            UserDTO user = authService.getCurrentUser(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @RequestBody Map<String, String> body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            String username = body.get("username");
            String bio = body.get("bio");
            String avatar = body.get("avatar");
            UserDTO user = authService.updateUser(userId, username, bio, avatar);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
