package com.douban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CollectionRequest {
    @NotBlank(message = "收藏状态不能为空")
    @Pattern(regexp = "^(wish|watching|watched)$", message = "状态只能是 wish、watching 或 watched")
    private String status;
}
