package com.douban.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStartupChecker implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(RedisStartupChecker.class);

    private final StringRedisTemplate redisTemplate;

    public RedisStartupChecker(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            String pong = redisTemplate.getConnectionFactory() != null
                    ? redisTemplate.getConnectionFactory().getConnection().ping()
                    : null;
            if (pong != null) {
                log.info("Redis connected (PING -> {})", pong);
            } else {
                log.warn("Redis connection factory not available");
            }
        } catch (Exception e) {
            log.warn("Redis not connected: {}", e.getMessage());
        }
    }
}

