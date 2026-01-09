-- 初始化数据库和基础数据（适用于本地调试）
DROP DATABASE IF EXISTS douban;

CREATE DATABASE douban DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE douban;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    avatar VARCHAR(500) DEFAULT 'https://img.icons8.com/fluency/96/user-male-circle.png',
    bio VARCHAR(500) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- 电影表
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    original_title VARCHAR(200),
    year INT,
    directors VARCHAR(500),
    actors VARCHAR(1000),
    genres VARCHAR(200),
    country VARCHAR(100),
    language VARCHAR(100),
    duration INT,
    rating DECIMAL(3, 1) DEFAULT 0.0,
    rating_count INT DEFAULT 0,
    summary TEXT,
    poster VARCHAR(500),
    images TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_year (year),
    INDEX idx_rating (rating DESC)
);

-- 评分表
CREATE TABLE IF NOT EXISTS ratings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    score INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_movie (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE
);

-- 评论表
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    title VARCHAR(200),
    content TEXT NOT NULL,
    likes INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE,
    INDEX idx_movie_id (movie_id),
    INDEX idx_user_id (user_id)
);

-- 收藏表
CREATE TABLE IF NOT EXISTS collections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    status ENUM('wish', 'watching', 'watched') NOT NULL DEFAULT 'wish',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_movie (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status)
);

-- SQL 审计表
CREATE TABLE IF NOT EXISTS sql_audit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL,
    client_ip VARCHAR(64) NULL,
    sql_text TEXT NOT NULL,
    blocked TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sql_audit_user (user_id),
    INDEX idx_sql_audit_blocked (blocked)
);

-- 公告表
CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    active TINYINT(1) DEFAULT 1,
    created_by BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 站内信/通知表
CREATE TABLE IF NOT EXISTS site_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    receiver_id BIGINT NOT NULL,
    sender_id BIGINT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    status ENUM('UNREAD', 'READ') DEFAULT 'UNREAD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_receiver_status (receiver_id, status)
);

-- 示例用户（密码哈希与 PasswordUtil 兼容）
INSERT INTO
    users (
        id,
        username,
        email,
        password_hash,
        role,
        status,
        bio
    )
VALUES (
        1,
        'admin',
        'admin@example.com',
        'SFMR3mCRFFCvyZ62nIVF5tr+8pr+1Utbj8lntFigTZnZIrdGE/hind1da5orxuhJ',
        'ADMIN',
        'ACTIVE',
        'Admin account'
    ),
    (
        2,
        'demo',
        'demo@example.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        'Demo user'
    ),
    (
        3,
        'movie_buff',
        'buff@example.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '资深影迷，一年看500部电影'
    ),
    (
        4,
        'cinephile_girl',
        'alice@example.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '喜欢文艺片和王家卫'
    ),
    (
        5,
        'tech_reviewer',
        'tech@example.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '硬核科幻迷，只看IMAX'
    ),
    (
        6,
        'OscarHunter',
        'oscar@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '奥斯卡小金人收集者'
    ),
    (
        7,
        'AnimeLover',
        'nime@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '为了二次元！'
    ),
    (
        8,
        'ScifiGuy',
        'future@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '专注于硬核科幻'
    ),
    (
        9,
        'HorrorFan',
        'ghost@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '喜欢在深夜看片'
    ),
    (
        10,
        'ActionJunkie',
        'boom@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '肾上腺素狂热者'
    ),
    (
        11,
        'RetroCine',
        'classic@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '黑白片的拥趸'
    ),
    (
        12,
        'IndieExplorer',
        'indie@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '拒绝主流，寻找独立之光'
    ),
    (
        13,
        'DramaQueen',
        'tear@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '每一部好电影都值得流泪'
    ),
    (
        14,
        'PopcornEater',
        'movie@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '爆米花电影万岁'
    ),
    (
        15,
        'CriticalEye',
        'review@ex.com',
        'p1P0zdKy4vfSKjAiCTafe6jlAbXfXLqR+oBAyRt5AwefFDC58kmvw4tjuoUI6MUS',
        'USER',
        'ACTIVE',
        '严苛的职业影评人'
    );

-- 示例电影
INSERT INTO
    movies (
        id,
        title,
        original_title,
        year,
        directors,
        actors,
        genres,
        country,
        language,
        duration,
        rating,
        rating_count,
        summary,
        poster
    )
VALUES (
        1,
        '肖申克的救赎',
        'The Shawshank Redemption',
        1994,
        'Frank Darabont',
        'Tim Robbins,Morgan Freeman',
        '剧情,犯罪',
        '美国',
        '英语',
        142,
        9.7,
        2000000,
        '银行家安迪入狱后与瑞德建立友谊并策划越狱。',
        'https://image.tmdb.org/t/p/w500/5YBgcdKcxGJJOULqsFWlU03vm3V.jpg'
    ),
    (
        2,
        '阿甘正传',
        'Forrest Gump',
        1994,
        'Robert Zemeckis',
        'Tom Hanks,Robin Wright',
        '剧情,爱情',
        '美国',
        '英语',
        142,
        9.5,
        1900000,
        '阿甘用自己的方式创造人生奇迹。',
        'https://image.tmdb.org/t/p/w500/yiWWUa1mB55dfd0QpGHsFsge67I.jpg'
    ),
    (
        3,
        '霸王别姬',
        'Farewell My Concubine',
        1993,
        '陈凯歌',
        '张国荣,张丰毅',
        '剧情,爱情',
        '中国',
        '汉语',
        171,
        9.6,
        1500000,
        '段小楼与程蝶衣的半生浮沉。',
        'https://image.tmdb.org/t/p/w500/vOEkLofQ8N1OdbGs5L87m7Plpw2.jpg'
    ),
    (
        4,
        '盗梦空间',
        'Inception',
        2010,
        'Christopher Nolan',
        'Leonardo DiCaprio,Joseph Gordon-Levitt',
        '剧情,科幻,悬疑',
        '美国,英国',
        '英语',
        148,
        9.4,
        1800000,
        '诺兰导演的烧脑神作，关于梦境、潜意识与现实的交织。',
        'https://image.tmdb.org/t/p/w500/lQEjWasu07JbQHdfFI5VnEUfId2.jpg'
    ),
    (
        5,
        '千与千寻',
        '千と千尋の神隠し',
        2001,
        '宫崎骏',
        '柊瑠美,入野自由',
        '剧情,动画,奇幻',
        '日本',
        '日语',
        125,
        9.4,
        2100000,
        '少女千寻意外进入神灵世界，开启一段自我寻找的旅程。',
        'https://image.tmdb.org/t/p/w500/b0c3TgnAqCfDLqzaAG2IoVxw3c4.jpg'
    ),
    (
        6,
        '蝙蝠侠：黑暗骑士',
        'The Dark Knight',
        2008,
        'Christopher Nolan',
        'Christian Bale,Heath Ledger',
        '剧情,动作,犯罪',
        '美国,英国',
        '英语',
        152,
        9.2,
        1100000,
        '小丑与蝙蝠侠的巅峰对决，深刻探讨人性善恶。',
        'https://image.tmdb.org/t/p/w500/xUX0sVHmukGTYLTyqmSF5hpktOU.jpg'
    ),
    (
        7,
        '星际穿越',
        'Interstellar',
        2014,
        'Christopher Nolan',
        'Matthew McConaughey,Anne Hathaway',
        '剧情,科幻,冒险',
        '美国,英国',
        '英语',
        169,
        9.4,
        1600000,
        '为了拯救地球，宇航员穿越虫洞寻找人类新家园。',
        'https://image.tmdb.org/t/p/w500/c35Vwd9rmMQfaEJuUrJRF3LZWJX.jpg'
    ),
    (
        8,
        '寄生虫',
        '기생충',
        2019,
        '奉俊昊',
        '宋康昊,李善均',
        '剧情,喜剧,惊悚',
        '韩国',
        '韩语',
        132,
        8.8,
        1200000,
        '贫穷家庭通过欺骗进入富人生活，引发的一连串悲剧。',
        'https://image.tmdb.org/t/p/w500/4DGPORlVIDIQvsuSDnM4uXKMjWS.jpg'
    ),
    (
        9,
        '教父',
        'The Godfather',
        1972,
        'Francis Ford Coppola',
        'Marlon Brando,Al Pacino',
        '剧情,犯罪',
        '美国',
        '英语',
        175,
        9.3,
        1200000,
        '黑帮史诗巨作，关于权力、家庭与背叛。',
        'https://image.tmdb.org/t/p/w500/y03tzUKvkRCYwJ5NWys4W4bnS9m.jpg'
    ),
    (
        10,
        '低俗小说',
        'Pulp Fiction',
        1994,
        'Quentin Tarantino',
        'John Travolta,Uma Thurman',
        '剧情,犯罪',
        '美国',
        '英语',
        154,
        8.9,
        980000,
        '环形叙事的开山之作，暴力美学的巅峰。',
        'https://image.tmdb.org/t/p/w500/WZW5PCtCbtyKfojdLlPCi89DNx.jpg'
    ),
    (
        11,
        '泰坦尼克号',
        'Titanic',
        1997,
        'James Cameron',
        'Leonardo DiCaprio,Kate Winslet',
        '剧情,爱情,灾难',
        '美国',
        '英语',
        194,
        9.5,
        2200000,
        '永恒的沉船绝恋，全球影史的奇迹。',
        'https://image.tmdb.org/t/p/w500/7ojgNNaZCDvp7IchH0uehUGJ0dH.jpg'
    ),
    (
        12,
        '你的名字。',
        '君の名は。',
        2016,
        '新海诚',
        '神木隆之介,上白石萌音',
        '剧情,动画,奇幻',
        '日本',
        '日语',
        106,
        8.5,
        1500000,
        '时空交错下的纯爱故事，寻找那个从未见过的你。',
        'https://image.tmdb.org/t/p/w500/vRQ7wpxpHt2aV8RnOkwHlOQAMKe.jpg'
    ),
    (
        13,
        '流浪地球2',
        'The Wandering Earth II',
        2023,
        '郭帆',
        '吴京,刘德华',
        '科幻,冒险,灾难',
        '中国',
        '汉语',
        173,
        8.3,
        1300000,
        '中国重工业科幻的里程碑，危难面前，唯有责任。',
        'https://image.tmdb.org/t/p/w500/cAS2e9hUwu6Ydsx7byXj16H00Ai.jpg'
    ),
    (
        14,
        '大话西游之大圣娶亲',
        'A Chinese Odyssey Part Two: Cinderella',
        1995,
        '刘镇伟',
        '周星驰,朱茵',
        '喜剧,爱情,奇幻',
        '中国香港',
        '粤语',
        95,
        9.2,
        1600000,
        '曾经有一份真诚的爱情摆在我的面前...',
        'https://image.tmdb.org/t/p/w500/32UL99Ec3HqbLZhaRPdEWM38IGy.jpg'
    ),
    (
        15,
        '楚门的世界',
        'The Truman Show',
        1998,
        'Peter Weir',
        'Jim Carrey,Laura Linney',
        '剧情,科幻',
        '美国',
        '英语',
        103,
        9.4,
        1700000,
        '如果你的生活是一场24小时直播的真人秀？',
        'https://image.tmdb.org/t/p/w500/nAnzFcqORitpwvRQPceIt4mcm8G.jpg'
    ),
    (
        16,
        '海上钢琴师',
        'La leggenda del pianista sull''oceano',
        1998,
        'Giuseppe Tornatore',
        'Tim Roth,Pruitt Taylor Vince',
        '剧情,音乐',
        '意大利',
        '英语',
        165,
        9.3,
        1400000,
        '一个出生在船上、一生从未踏上陆地的天才琴师。',
        'https://image.tmdb.org/t/p/w500/le7jQ6Sc3xEUvosHXJojDMR6EnE.jpg'
    ),
    (
        17,
        '让子弹飞',
        'Let the Bullets Fly',
        2010,
        '姜文',
        '姜文,葛优,周润发',
        '剧情,喜剧,动作',
        '中国',
        '汉语',
        132,
        9.0,
        1800000,
        '站着把钱挣了！申曲与鹅城的权力博弈。',
        'https://image.tmdb.org/t/p/w500/vUvrjRRmC1BeI8QyMaRBdtIqHHn.jpg'
    ),
    (
        18,
        '辛德勒的名单',
        'Schindler''s List',
        1993,
        'Steven Spielberg',
        'Liam Neeson,Ben Kingsley',
        '剧情,历史,战争',
        '美国',
        '英语',
        195,
        9.6,
        1100000,
        '在黑暗的深渊中，人性发出了最耀眼的光芒。',
        'https://image.tmdb.org/t/p/w500/7VR9AXYa3VYF20jDWfI7w9sSJ2D.jpg'
    ),
    (
        19,
        '疯狂的麦克斯4',
        'Mad Max: Fury Road',
        2015,
        'George Miller',
        'Tom Hardy,Charlize Theron',
        '动作,科幻,冒险',
        '澳大利亚,美国',
        '英语',
        120,
        8.7,
        850000,
        '废土美学的极致，狂暴之路上的生存挣扎。',
        'https://image.tmdb.org/t/p/w500/jGMjF2SRawSsp8taI7BL8cC2Gp7.jpg'
    ),
    (
        20,
        '瞬息全宇宙',
        'Everything Everywhere All at Once',
        2022,
        'Daniel Kwan',
        'Michelle Yeoh,Ke Huy Quan',
        '喜剧,奇幻,冒险',
        '美国',
        '英语',
        139,
        7.6,
        950000,
        '关于爱与虚无的多元宇宙大冒险。',
        'https://image.tmdb.org/t/p/w500/p8ulGTtp2Syv2U5H32FcWoBQb9v.jpg'
    );
-- 示例评分与收藏
INSERT INTO
    ratings (user_id, movie_id, score)
VALUES (2, 1, 10),
    (2, 2, 9),
    (3, 1, 10),
    (3, 4, 10),
    (3, 7, 9),
    (3, 6, 9),
    (4, 3, 10),
    (4, 5, 10),
    (4, 8, 8),
    (5, 4, 10),
    (5, 7, 10),
    (5, 6, 8),
    -- 为《教父》刷高分
    (6, 9, 10),
    (7, 9, 9),
    (8, 9, 10),
    (15, 9, 10),
    -- 为《泰坦尼克号》增加热度
    (6, 11, 10),
    (7, 11, 9),
    (10, 11, 10),
    (14, 11, 8),
    (13, 11, 10),
    -- 为《流浪地球2》增加中性评分
    (8, 13, 10),
    (10, 13, 8),
    (15, 13, 9),
    (6, 13, 8),
    -- 为《瞬息全宇宙》增加争议评分
    (15, 20, 6),
    (12, 20, 10),
    (10, 20, 7),
    (3, 20, 8),
    -- 更多杂项评分
    (11, 16, 10),
    (12, 17, 9),
    (13, 14, 10),
    (14, 12, 8),
    (7, 5, 10);

INSERT INTO
    collections (user_id, movie_id, status)
VALUES (2, 1, 'watched'),
    (2, 2, 'watching'),
    (3, 4, 'watched'),
    (3, 5, 'wish'),
    (3, 6, 'watched'),
    (4, 1, 'watched'),
    (4, 7, 'wish'),
    (4, 8, 'watched'),
    (5, 7, 'watched'),
    (5, 4, 'watching'),
    (5, 6, 'watched'),
    (6, 9, 'watched'),
    (6, 18, 'watched'),
    (6, 16, 'wish'),
    (7, 12, 'watched'),
    (7, 5, 'watched'),
    (7, 14, 'watching'),
    (8, 13, 'watched'),
    (8, 4, 'watched'),
    (8, 7, 'watched'),
    (15, 17, 'watched'),
    (15, 10, 'watched'),
    (15, 20, 'watched'),
    (10, 19, 'watched'),
    (10, 6, 'watched'),
    (14, 11, 'watched'),
    (14, 1, 'watched'),
    (13, 11, 'watched'),
    (12, 17, 'watched');

-- 示例评论
INSERT INTO
    reviews (
        user_id,
        movie_id,
        title,
        content,
        likes
    )
VALUES (
        2,
        1,
        '永远的经典',
        '剧情和表演都无可挑剔，百看不厌。',
        3
    ),
    (
        3,
        4,
        '梦境的建筑学',
        '诺兰用严谨的逻辑构建了一个多层梦境。不仅是视觉奇观，更是对潜意识的深刻探索。',
        45
    ),
    (
        4,
        5,
        '被名字束缚的人生',
        '宫崎骏用最绚烂的画面讲了一个最悲伤也最温暖的故事：不要忘记自己的名字。',
        32
    ),
    (
        5,
        7,
        '爱是唯一跨越时空的维度',
        '物理学让这部电影有了骨架，而父女情让它有了灵魂。',
        88
    ),
    (
        3,
        8,
        '地下室的阳光',
        '阶级的鸿沟不是靠几张伪造的学历就能填平的，气味才是最诚实的标签。',
        21
    ),
    (
        6,
        9,
        '无法超越的男人的圣经',
        '这不是一部简单的黑帮片，这是关于生活、责任和克制的哲学课。',
        156
    ),
    (
        15,
        17,
        '鹅城岁月的隐喻',
        '姜文把所有的政治隐喻都藏在了狂欢式的台词里，每看一遍都有新发现。',
        289
    ),
    (
        13,
        11,
        '纸巾准备好了吗',
        '小时候看的是特效，长大了看的是在那场灾难面前，人性能迸发出怎样的光。',
        420
    ),
    (
        11,
        16,
        '陆地对我来说是一艘太大的船',
        '1900拒绝下船的那一刻，他守护的是自己的纯粹，而我们都成了陆地的囚徒。',
        198
    ),
    (
        8,
        13,
        '中国科幻的黄金时代开启了',
        '不论是数字生命还是移山计划，都展现了中国科幻独有的家国情怀。',
        350
    ),
    (
        12,
        20,
        '妈的多元宇宙',
        '疯狂的外壳下包裹着一个最核心的主题：母女的和解，以及在虚无中拥抱平庸。',
        88
    ),
    (
        14,
        14,
        '笑中带泪的极致',
        '小时候看笑了，长大了看哭了。至尊宝转身的那一刻，就是成长的代价。',
        510
    );

-- 示例公告与站内信
INSERT INTO
    announcements (
        title,
        content,
        active,
        created_by
    )
VALUES (
        '欢迎使用 Douban Clone',
        '这是演示公告，管理员可在后台发布/下线。',
        1,
        1
    ),
    (
        '关于社区文明发评的规范',
        '请各位影迷理性讨论，禁止人身攻击。',
        1,
        1
    ),
    (
        '系统维护通知',
        '本站将于本周日凌晨2点进行数据库升级。',
        0,
        1
    );

INSERT INTO
    site_messages (
        receiver_id,
        sender_id,
        title,
        content,
        status
    )
VALUES (
        2,
        1,
        '欢迎',
        '欢迎体验站内信与推送功能。',
        'UNREAD'
    ),
    (
        3,
        1,
        '实名认证成功',
        '您的影评人认证申请已通过，现在可以发布长评了。',
        'READ'
    ),
    (
        4,
        2,
        '评论获赞提醒',
        'demo 赞了你的评论《被名字束缚的人生》',
        'UNREAD'
    ),
    (
        5,
        1,
        '版权声明',
        '由于版权调整，部分预告片可能暂时无法播放。',
        'UNREAD'
    );
