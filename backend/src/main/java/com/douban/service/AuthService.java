package com.douban.service;

import com.douban.dto.*;
import com.douban.entity.User;
import com.douban.mapper.UserMapper;
import com.douban.util.JwtUtil;
import com.douban.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        // 检查邮箱是否已存在
        if (userMapper.countByEmail(request.getEmail()) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(PasswordUtil.hashPassword(request.getPassword()));
        user.setAvatar("https://img.icons8.com/fluency/96/user-male-circle.png");
        user.setBio("");

        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(token, UserDTO.fromEntity(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            user = userMapper.findByEmail(request.getUsername());
        }

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(token, UserDTO.fromEntity(user));
    }

    public UserDTO getCurrentUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return UserDTO.fromEntity(user);
    }

    public UserDTO updateUser(Long userId, String username, String bio, String avatar) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (username != null && !username.equals(user.getUsername())) {
            if (userMapper.countByUsername(username) > 0) {
                throw new RuntimeException("用户名已存在");
            }
            user.setUsername(username);
        }
        if (bio != null) {
            user.setBio(bio);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }

        userMapper.update(user);
        return UserDTO.fromEntity(user);
    }
}
