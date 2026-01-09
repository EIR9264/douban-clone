package com.douban.controller;

import com.douban.entity.Announcement;
import com.douban.mapper.AnnouncementMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementMapper announcementMapper;

    public AnnouncementController(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    @GetMapping
    public List<Announcement> listActive() {
        return announcementMapper.findActive();
    }
}

