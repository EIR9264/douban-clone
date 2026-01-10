package com.douban.dto;

import lombok.Data;

@Data
public class AdminUpdateUserRequest {
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private String role;
    private String status;
}

