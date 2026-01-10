-- 用于“已存在数据库”的手动迁移脚本（不要在 init.sql 的全量重建场景使用）
-- 目标：
-- 1) 以 review_likes 表作为点赞唯一真相（COUNT 聚合）
-- 2) 删除 reviews.likes 列（避免双写导致不一致）

-- 0) 确认当前库
-- USE douban;

-- 1) 如不存在则创建 review_likes 表
CREATE TABLE IF NOT EXISTS review_likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_review (user_id, review_id),
    INDEX idx_review (review_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE
);

-- 2) 如果你的 reviews 表仍然存在 likes 列，请先执行：
-- SHOW COLUMNS FROM reviews LIKE 'likes';
-- 如果有返回，再执行：
-- ALTER TABLE reviews DROP COLUMN likes;

-- 注意：如果你之前把点赞数写在 reviews.likes 里，这里无法“还原”为用户维度的点赞记录（缺少用户ID）。
-- 从此以后，点赞数会完全由 review_likes 的实际记录 COUNT 得到。
