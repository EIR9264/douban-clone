# 本地依赖服务（Nacos / Redis）

本项目后端已支持 Nacos（配置中心 + 注册发现）与 Redis；你本地已有 MySQL，所以这里不包含 MySQL 的 docker。

## 启动服务

在仓库根目录执行：

```bash
docker compose up -d
```

查看状态：

```bash
docker compose ps
```

停止服务：

```bash
docker compose down
```

## 访问地址

- Nacos: `http://localhost:8848/nacos`
- Redis: `localhost:6379`

## 后端连接方式（默认）

后端默认从以下地址读取（可通过环境变量覆盖）：

- Nacos：`NACOS_SERVER=localhost:8848`
- Redis：`spring.data.redis.host=localhost`，`spring.data.redis.port=6379`

## 启用 Nacos（可选）

默认启动后端不会连接 Nacos（避免你没启动 Nacos 时直接启动失败）。

当你启动了 Nacos 后，用 profile 启用：

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,nacos
```

或运行 jar：

```bash
java -jar target/douban-movie-1.0.0.jar --spring.profiles.active=dev,nacos
```

注意：如果你看到日志里是 `No active profile set`，说明你实际上并没有启用 `nacos` profile，这时应用不会连接 Nacos（也不会从 Nacos 读取配置）。

如果要把配置放到 Nacos：

- 在 Nacos 配置中心创建 `DataId`：`douban-movie-dev.yaml`
- 或按 `backend/src/main/resources/application-nacos.yml` 的 import 规则创建对应 DataId

说明：Nacos 相关配置已移动到 `backend/src/main/resources/application-nacos.yml`，不再使用 `bootstrap.yml`。
