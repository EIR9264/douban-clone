package com.douban.controller;

import com.douban.dto.ChangePasswordRequest;
import com.douban.dto.UserDTO;
import com.douban.service.AuthService;
import com.douban.service.ObjectStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectStorageService storageService;

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

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @Valid @RequestBody ChangePasswordRequest body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            authService.changePassword(userId, body.getOldPassword(), body.getNewPassword());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            ObjectStorageService.UploadedObject obj = storageService.upload(file, "avatars/" + userId);
            UserDTO updated = authService.updateUser(userId, null, null, obj.url());
            return ResponseEntity.ok(Map.of("url", obj.url(), "objectKey", obj.objectKey(), "user", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
