package com.douban.controller.admin;

import com.douban.dto.MessageRequest;
import com.douban.entity.SiteMessage;
import com.douban.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/messages")
public class AdminMessageController {

    private final NotificationService notificationService;

    public AdminMessageController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public SiteMessage send(@RequestAttribute("userId") Long adminId, @RequestBody MessageRequest request) {
        return notificationService.sendToUser(request.getReceiverId(), adminId, request.getTitle(), request.getContent());
    }

    @PostMapping("/broadcast")
    public Map<String, String> broadcast(@RequestAttribute("userId") Long adminId, @RequestBody MessageRequest request) {
        notificationService.broadcast(adminId, request.getTitle(), request.getContent());
        return Map.of("message", "已广播");
    }
}
