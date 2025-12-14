package com.douban.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> items;
    private int page;
    private int size;
    private long total;
    private int totalPages;

    public PageResult(List<T> items, int page, int size, long total) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
    }
}
