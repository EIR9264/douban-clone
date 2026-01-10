package com.douban.service;

import com.douban.dto.CaptchaResponse;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {
    private static final Duration TTL = Duration.ofMinutes(3);
    private static final int EXPIRES_IN_SECONDS = (int) TTL.toSeconds();
    private static final SecureRandom random = new SecureRandom();

    private record Entry(String code, long expiresAtMs) {}

    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();

    public CaptchaResponse create() {
        cleanupExpired();

        String captchaId = UUID.randomUUID().toString();
        String code = randomCode(4);
        long expiresAt = System.currentTimeMillis() + TTL.toMillis();
        store.put(captchaId, new Entry(code, expiresAt));

        String image = "data:image/png;base64," + renderBase64Png(code);
        return new CaptchaResponse(captchaId, image, EXPIRES_IN_SECONDS);
    }

    public boolean verifyAndConsume(String captchaId, String captchaCode) {
        cleanupExpired();
        if (captchaId == null || captchaCode == null) return false;
        Entry entry = store.remove(captchaId);
        if (entry == null) return false;
        if (System.currentTimeMillis() > entry.expiresAtMs()) return false;
        return entry.code().equalsIgnoreCase(captchaCode.trim());
    }

    private void cleanupExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Entry>> it = store.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Entry> e = it.next();
            if (e.getValue().expiresAtMs() <= now) {
                it.remove();
            }
        }
    }

    private String randomCode(int len) {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String renderBase64Png(String text) {
        try {
            int width = 120;
            int height = 44;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(new Color(245, 247, 250));
            g.fillRect(0, 0, width, height);

            for (int i = 0; i < 10; i++) {
                g.setColor(new Color(200 + random.nextInt(40), 200 + random.nextInt(40), 200 + random.nextInt(40)));
                int x1 = random.nextInt(width);
                int y1 = random.nextInt(height);
                int x2 = random.nextInt(width);
                int y2 = random.nextInt(height);
                g.drawLine(x1, y1, x2, y2);
            }

            g.setFont(new Font("SansSerif", Font.BOLD, 26));
            g.setColor(new Color(34, 34, 34));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (width - textWidth) / 2;
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(text, x, y);

            g.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }
}

