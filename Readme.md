# Douban Clone (Spring Boot + Vue)

一个简洁的电影评分/收藏系统示例，后端 Spring Boot + MyBatis + Redis，前端 Vue 3 + Element Plus，并内置管理员后台、站内推送（WebSocket + STOMP）。

## 功能速览
- 用户：注册/登录、个人资料、电影收藏、评分、评论
- 管理员：电影 CRUD、用户角色/状态管理、评论删除、公告发布、站内信
- 推送：WebSocket 通知（用户队列 + 公告广播）
- 安全：JWT（含角色）、MyBatis SQL 审计拦截、可选 Nacos 配置中心/注册发现

## 目录结构
- `backend/`：Spring Boot 服务
- `frontend/`：Vite + Vue 3 前端
- `docs/`：开发辅助文档与初始化 SQL

## 快速开始
### 依赖
- JDK 21
- Node.js 20+
- MySQL（本地已存在，端口 3306，用户名/密码按需修改 `backend/src/main/resources/application.yml`）
- Redis（可用 Docker 运行，见下文）
- 可选：Nacos（Docker 运行，启动后用 `--spring.profiles.active=dev,nacos` 启用）

### 初始化数据库
执行 `docs/init.sql`（会创建库 `douban`、表、并插入演示数据）：
```bash
# MySQL 控制台
source /path/to/docs/init.sql;
```
管理员账号：`admin` / `admin123`，普通用户：`demo` / `demo123`

### 启动依赖服务（Redis / Nacos 可选）
仓库根目录：
```bash
docker compose up -d   # 启动 nacos:8848, redis:6379
```
不需要 MySQL（脚本已假设本地已有）。

### 启动后端
```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev      # 不用 Nacos
# 或
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,nacos # 启用 Nacos
```

### 启动前端
```bash
cd frontend
npm install
npm run dev
```
默认前端端口 5173，已代理到后端 8080。

## 其他说明
- SQL 审计拦截：拦截可疑 SQL 并写入表 `sql_audit`
- WebSocket：STOMP 端点 `/api/ws`，用户订阅 `/user/queue/notice`，公告 `/topic/announcement`
- 依赖服务文档：`docs/dev-services.md`

## 常用命令
- 后端打包：`cd backend && ./mvnw -DskipTests package`
- 前端构建：`cd frontend && npm run build`
