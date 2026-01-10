package com.douban.service;

import com.douban.entity.SiteMessage;
import com.douban.dto.PageResult;
import com.douban.mapper.SiteMessageMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final SiteMessageMapper siteMessageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SiteMessageMapper siteMessageMapper, SimpMessagingTemplate messagingTemplate) {
        this.siteMessageMapper = siteMessageMapper;
        this.messagingTemplate = messagingTemplate;
    }

    public SiteMessage sendToUser(Long receiverId, Long senderId, String title, String content) {
        if (receiverId == null) {
            throw new IllegalArgumentException("receiverId不能为空");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        SiteMessage message = new SiteMessage();
        message.setReceiverId(receiverId);
        message.setSenderId(senderId);
        message.setTitle(title);
        message.setContent(content);
        message.setStatus("UNREAD");
        message.setCreatedAt(LocalDateTime.now());

        siteMessageMapper.insert(message);
        messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/notice", message);
        return message;
    }

    /**
     * 简单广播：发送到/topic/announcement，前端可订阅；同时不入库（避免为每个用户插入记录）。
     */
    public void broadcast(Long senderId, String title, String content) {
        SiteMessage message = new SiteMessage();
        message.setReceiverId(0L);
        message.setSenderId(senderId);
        message.setTitle(title);
        message.setContent(content);
        message.setStatus("UNREAD");
        messagingTemplate.convertAndSend("/topic/announcement", message);
    }

    public List<SiteMessage> listUnread(Long userId) {
        return siteMessageMapper.findUnread(userId);
    }

    public List<SiteMessage> list(Long userId, int page, int size) {
        int offset = Math.max(0, (page - 1) * size);
        return siteMessageMapper.findByReceiver(userId, size, offset);
    }

    public PageResult<SiteMessage> listPage(Long userId, int page, int size) {
        int safePage = Math.max(1, page);
        int safeSize = Math.min(50, Math.max(1, size));
        int offset = (safePage - 1) * safeSize;
        List<SiteMessage> items = siteMessageMapper.findByReceiver(userId, safeSize, offset);
        int total = siteMessageMapper.countByReceiver(userId);
        return new PageResult<>(items, safePage, safeSize, total);
    }

    public void markRead(Long userId, List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            siteMessageMapper.markRead(userId, ids);
        }
    }

    public int countUnread(Long userId) {
        return siteMessageMapper.countUnread(userId);
    }
}
