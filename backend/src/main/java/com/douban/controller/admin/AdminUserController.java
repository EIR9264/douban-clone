package com.douban.controller.admin;

import com.douban.dto.AdminUpdateUserRequest;
import com.douban.dto.PageResult;
import com.douban.dto.UpdateRoleRequest;
import com.douban.dto.UpdateStatusRequest;
import com.douban.dto.UserDTO;
import com.douban.entity.User;
import com.douban.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserMapper userMapper;

    public AdminUserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping
    public PageResult<UserDTO> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        int offset = Math.max(0, (page - 1) * size);
        List<User> users = userMapper.findAll(size, offset);
        List<UserDTO> dtos = users.stream().map(UserDTO::fromEntity).collect(Collectors.toList());
        int total = userMapper.countAll();
        return new PageResult<>(dtos, page, size, total);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        try {
            User user = userMapper.findById(id);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "用户不存在"));
            }
            if ("ADMIN".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(request.getRole())) {
                int adminCount = userMapper.countByRole("ADMIN");
                if (adminCount <= 1) {
                    return ResponseEntity.badRequest().body(Map.of("error", "管理员数量不可小于 1"));
                }
            }
            userMapper.updateRole(id, request.getRole());
            return ResponseEntity.ok(Map.of("message", "角色已更新"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        try {
            User user = userMapper.findById(id);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "用户不存在"));
            }
            userMapper.updateStatus(id, request.getStatus());
            return ResponseEntity.ok(Map.of("message", "状态已更新"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AdminUpdateUserRequest request) {
        try {
            User user = userMapper.findById(id);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "用户不存在"));
            }

            if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
                if (userMapper.countByUsername(request.getUsername()) > 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "用户名已存在"));
                }
                user.setUsername(request.getUsername());
            }

            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                if (userMapper.countByEmail(request.getEmail()) > 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "邮箱已被注册"));
                }
                user.setEmail(request.getEmail());
            }

            if (request.getAvatar() != null) {
                user.setAvatar(request.getAvatar());
            }
            if (request.getBio() != null) {
                user.setBio(request.getBio());
            }

            userMapper.update(user);

            if (request.getStatus() != null) {
                userMapper.updateStatus(id, request.getStatus());
            }

            if (request.getRole() != null) {
                if ("ADMIN".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(request.getRole())) {
                    int adminCount = userMapper.countByRole("ADMIN");
                    if (adminCount <= 1) {
                        return ResponseEntity.badRequest().body(Map.of("error", "管理员数量不可小于 1"));
                    }
                }
                userMapper.updateRole(id, request.getRole());
            }

            return ResponseEntity.ok(UserDTO.fromEntity(userMapper.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
