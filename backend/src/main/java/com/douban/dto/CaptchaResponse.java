package com.douban.dto;

import lombok.Data;

@Data
public class CaptchaResponse {
    private String captchaId;
    private String image; // data:image/png;base64,...
    private int expiresIn; // seconds

    public CaptchaResponse(String captchaId, String image, int expiresIn) {
        this.captchaId = captchaId;
        this.image = image;
        this.expiresIn = expiresIn;
    }
}

