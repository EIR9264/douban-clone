package com.douban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewRequest {
    @Size(max = 200, message = "标题最长200个字符")
    private String title;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 5000, message = "评论最长5000个字符")
    private String content;
}
