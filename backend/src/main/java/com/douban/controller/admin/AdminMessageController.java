package com.douban.controller.admin;

import com.douban.dto.MessageRequest;
import com.douban.entity.SiteMessage;
import com.douban.service.NotificationService;
import org.springframework.web.bind.annotation.*;

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
}
