package com.douban.controller.admin;

import com.douban.dto.AnnouncementRequest;
import com.douban.entity.Announcement;
import com.douban.mapper.AnnouncementMapper;
import com.douban.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/announcements")
public class AdminAnnouncementController {

    private final AnnouncementMapper announcementMapper;
    private final NotificationService notificationService;

    public AdminAnnouncementController(AnnouncementMapper announcementMapper, NotificationService notificationService) {
        this.announcementMapper = announcementMapper;
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Announcement> list() {
        return announcementMapper.findAll();
    }

    @PostMapping
    public Announcement create(@RequestAttribute("userId") Long adminId, @RequestBody AnnouncementRequest request) {
        Announcement a = new Announcement();
        a.setTitle(request.getTitle());
        a.setContent(request.getContent());
        a.setActive(request.getActive());
        a.setCreatedBy(adminId);
        announcementMapper.insert(a);
        notificationService.broadcast(adminId, "[公告]" + a.getTitle(), a.getContent());
        return a;
    }

    @PutMapping("/{id}/active")
    public Map<String, String> updateStatus(@PathVariable Long id, @RequestParam boolean active) {
        announcementMapper.updateStatus(id, active);
        return Map.of("message", "状态已更新");
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        announcementMapper.delete(id);
        return Map.of("message", "已删除");
    }
}
