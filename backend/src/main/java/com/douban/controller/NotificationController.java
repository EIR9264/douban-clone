package com.douban.controller;

import com.douban.entity.SiteMessage;
import com.douban.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/unread")
    public List<SiteMessage> unread(@RequestAttribute("userId") Long userId) {
        return notificationService.listUnread(userId);
    }

    @GetMapping
    public List<SiteMessage> list(@RequestAttribute("userId") Long userId,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        return notificationService.list(userId, page, size);
    }

    @PostMapping("/read")
    public void markRead(@RequestAttribute("userId") Long userId, @RequestBody List<Long> ids) {
        // 用户ID已在拦截器校验，这里仅做消息ID批量已读
        notificationService.markRead(ids);
    }
}
