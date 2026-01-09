package com.douban.controller.admin;

import com.douban.dto.PageResult;
import com.douban.dto.UpdateRoleRequest;
import com.douban.dto.UpdateStatusRequest;
import com.douban.dto.UserDTO;
import com.douban.entity.User;
import com.douban.mapper.UserMapper;
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
    public Map<String, String> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        userMapper.updateRole(id, request.getRole());
        return Map.of("message", "角色已更新");
    }

    @PutMapping("/{id}/status")
    public Map<String, String> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        userMapper.updateStatus(id, request.getStatus());
        return Map.of("message", "状态已更新");
    }
}
