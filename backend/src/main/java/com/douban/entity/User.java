package com.douban.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String avatar;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
