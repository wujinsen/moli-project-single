# AWS 部署指南（MySQL + Nginx + Redis）

适用项目：**moli-project-single**（棠羽管理系统后端）+ 独立 Vue 前端。

本文以 **单台 EC2 自建 MySQL / Redis / Nginx** 为推荐方案，成本低、与 Shiro + Jedis 兼容性好。生产规模扩大后可再拆分为 RDS + ElastiCache。

---

## 1. 架构概览

```
Internet
   │
   ▼
┌────────────────────────────────────────────── EC2 ──────────────────────────────────────────────┐
│                                                                                                 │
│   Nginx :80 / :443                                                                              │
│     ├─ admin.wu-jinsen.com  →  /opt/moli/frontend/dist   （Vue 静态资源）                        │
│     └─ api.wu-jinsen.com    →  127.0.0.1:8888            （Spring Boot 反代，可选同域方案）      │
│                                                                                                 │
│   Java moli-server :8888                                                                        │
│     ├─ MySQL  :3306  （本机 127.0.0.1）                                                         │
│     └─ Redis  :6379  （本机 127.0.0.1）                                                         │
│                                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────────────────────┘
```

| 组件 | 推荐部署 | 说明 |
|------|----------|------|
| 前端 | Nginx 静态托管 | 构建产物放 `/opt/moli/frontend/dist` |
| 后端 | EC2 + systemd | JAR 放 `/opt/moli/backend` |
| MySQL | EC2 自建 或 RDS | 小项目可先同机 |
| Redis | **EC2 自建** | **不要用 ElastiCache Serverless**（不支持 SELECT/SCAN，会导致 Shiro 登录失败） |
| 对象存储 | S3（可选） | 替代 MinIO |

---

## 2. 前置准备

- AWS 账号（建议区域：新加坡 `ap-southeast-1` 或悉尼 `ap-southeast-2`）
- 域名（示例：`wu-jinsen.com`）
- 本地已生成 SSH 密钥对（`.pem`）
- 本地可构建：`mvn package`（后端）、`npm run build`（前端）

---

## 3. 创建 EC2 实例

### 3.1 启动实例

控制台 → **EC2** → **启动实例**：

| 配置项 | 建议 |
|--------|------|
| 名称 | `moli-prod` |
| AMI | Amazon Linux 2023 或 Ubuntu 22.04 |
| 实例类型 | `t3.small`（2C2G） |
| 密钥对 | 新建并下载 `.pem` |
| 存储 | 30 GB gp3 |

### 3.2 安全组入站规则

| 类型 | 端口 | 来源 | 用途 |
|------|------|------|------|
| SSH | 22 | 你的 IP | 运维 |
| HTTP | 80 | 0.0.0.0/0 | Web |
| HTTPS | 443 | 0.0.0.0/0 | Web（配置证书后） |

> **不要**对公网开放 3306（MySQL）、6379（Redis）、8888（Java）。这些服务只监听 `127.0.0.1`。

### 3.3 SSH 登录

```bash
ssh -i "你的密钥.pem" ec2-user@EC2公网IP
# Ubuntu 用户名为 ubuntu
```

---

## 4. 安装基础环境

### Amazon Linux 2023

```bash
sudo dnf update -y
sudo dnf install -y java-11-amazon-corretto-headless nginx redis6 mysql-server
```

### Ubuntu 22.04

```bash
sudo apt update -y
sudo apt install -y openjdk-11-jdk nginx redis-server mysql-server
```

验证 Java：

```bash
java -version
```

---

## 5. 目录规划

```bash
sudo mkdir -p /opt/moli/{frontend/dist,backend,logs,sql}
sudo chown -R $USER:$USER /opt/moli
```

| 路径 | 用途 |
|------|------|
| `/opt/moli/frontend/dist` | 前端 `npm run build` 产物 |
| `/opt/moli/backend` | `moli-server.jar`、生产配置 |
| `/opt/moli/logs` | 应用日志（可选） |
| `/opt/moli/sql` | 建表与种子 SQL |

---

## 6. 部署 MySQL

### 6.1 启动并设置开机自启

**Amazon Linux：**

```bash
sudo systemctl enable mysqld
sudo systemctl start mysqld
```

**Ubuntu：**

```bash
sudo systemctl enable mysql
sudo systemctl start mysql
```

### 6.2 安全配置

```bash
sudo mysql_secure_installation
```

按提示设置 root 密码、删除匿名用户、禁止 root 远程登录。

### 6.3 创建库与用户

```bash
sudo mysql -u root -p
```

```sql
CREATE DATABASE moli DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE USER 'moli'@'localhost' IDENTIFIED BY '你的强密码';
GRANT ALL PRIVILEGES ON moli.* TO 'moli'@'localhost';
FLUSH PRIVILEGES;
```

### 6.4 导入表结构与种子数据

将仓库中 `docs/sql/00_schema.sql`、`docs/sql/01_baseline_data.sql` 上传到服务器后执行：

```bash
mysql -u moli -p moli < /opt/moli/docs/sql/00_schema.sql
mysql -u moli -p moli < /opt/moli/docs/sql/01_baseline_data.sql
```

### 6.5 确认仅本机访问

```bash
sudo ss -lntp | grep 3306
```

应看到 `127.0.0.1:3306`，而非 `0.0.0.0:3306`。

---

## 7. 部署 Redis

> **重要**：本项目使用 Shiro + `shiro-redis` + Jedis，需要 Redis 支持 `SELECT`、`SCAN` 等标准命令。  
> **请勿使用 AWS ElastiCache Serverless**，否则登录会出现：
>
> - `ERR unknown command 'SELECT'`
> - `ERR unknown command 'SCAN'`

### 7.1 配置密码（Amazon Linux）

```bash
sudo vi /etc/redis6/redis6.conf
```

修改（**systemd 托管时必须 `daemonize no`**，否则 `systemctl restart redis6` 会失败）：

```conf
bind 127.0.0.1
supervised systemd
daemonize no
requirepass 你的Redis密码
```

```bash
sudo systemctl enable redis6
sudo systemctl restart redis6
sudo systemctl status redis6 --no-pager
```

若仍失败，先看日志：

```bash
sudo journalctl -u redis6 -n 50 --no-pager
sudo redis-server /etc/redis6/redis6.conf --test-memory 1
```

### 7.2 配置密码（Ubuntu）

```bash
sudo vi /etc/redis/redis.conf
```

同样设置 `bind 127.0.0.1` 与 `requirepass`，然后：

```bash
sudo systemctl enable redis-server
sudo systemctl restart redis-server
```

### 7.3 验证

```bash
# Amazon Linux 2023 客户端名为 redis6-cli（不是 redis-cli）
redis6-cli -a 你的Redis密码 ping
# 期望：PONG

redis6-cli -a 你的Redis密码
127.0.0.1:6379> SELECT 0
OK
```

---

## 8. 部署后端（Spring Boot）

### 8.1 本地打包

```bash
cd moli-parent && mvn -DskipTests install && cd ..
mvn -pl moli-common,moli-server -am -DskipTests package
```

产物：`moli-server/target/moli-server-1.0-SNAPSHOT.jar`

### 8.2 上传到 EC2

```bash
scp -i "你的密钥.pem" moli-server/target/moli-server-1.0-SNAPSHOT.jar \
  ec2-user@EC2公网IP:/opt/moli/backend/moli-server.jar
```

### 8.3 生产配置

在服务器创建 `/opt/moli/backend/application-pro.yml`（**勿提交 Git**）：

```bash
cp moli-server/src/main/resources/application-pro.yml.example \
   /opt/moli/backend/application-pro.yml
```

或通过环境变量注入（推荐）：

```bash
export SPRING_PROFILES_ACTIVE=pro
export DB_HOST=127.0.0.1
export DB_PORT=3306
export DB_NAME=moli
export SPRING_DATASOURCE_USERNAME=moli
export SPRING_DATASOURCE_PASSWORD=你的MySQL密码
export SPRING_REDIS_HOST=127.0.0.1
export SPRING_REDIS_PORT=6379
export SPRING_REDIS_PASSWORD=你的Redis密码
export SPRING_REDIS_DATABASE=0
```

### 8.4 启停脚本与 systemd

项目自带 Linux 脚本（`scripts/linux/`）：

```bash
# 上传到服务器
scp -i "你的密钥.pem" scripts/linux/moli-server.sh scripts/linux/moli-server.env.example \
  ec2-user@EC2公网IP:/opt/moli/backend/

ssh -i "你的密钥.pem" ec2-user@EC2公网IP <<'EOF'
cd /opt/moli/backend
mkdir -p conf logs run
cp moli-server.env.example conf/moli-server.env
chmod 600 conf/moli-server.env
chmod +x moli-server.sh
vi conf/moli-server.env   # 修改密码、SSO_SHARED_SECRET 等
./moli-server.sh start
./moli-server.sh status
EOF
```

常用命令：`start` / `stop` / `restart` / `status`

**开机自启（systemd）**：

```bash
sudo cp /opt/moli/backend/../scripts/linux/moli-server.service /etc/systemd/system/moli-server.service
# 或从仓库复制 scripts/linux/moli-server.service 到服务器后：
sudo cp moli-server.service /etc/systemd/system/moli-server.service
sudo systemctl daemon-reload
sudo systemctl enable moli-server
sudo systemctl start moli-server
sudo systemctl status moli-server
```

`moli-server.service` 会读取 `/opt/moli/backend/conf/moli-server.env`，并调用 `moli-server.sh` 启停。

### 8.5 验证后端

```bash
curl -s http://127.0.0.1:8888/login \
  -H "Content-Type: application/json" \
  -d '{"userName":"admin","password":"你的密码"}' | head
```

成功应返回 `code: 200` 与 `token`。

---

## 9. 部署 Nginx

### 9.1 上传前端

本地构建后上传：

```bash
npm run build
rsync -avz --delete -e "ssh -i 你的密钥.pem" dist/ \
  ec2-user@EC2公网IP:/opt/moli/frontend/dist/
```

前端生产环境变量示例（按实际域名修改）：

```env
# 方案 A：API 独立子域
VUE_APP_BASE_API=https://api.wu-jinsen.com

# 方案 B：同域反代（见 9.3 合并配置）
VUE_APP_BASE_API=
```

### 9.2 管理后台站点（admin）

```bash
sudo vi /etc/nginx/conf.d/moli-admin.conf
```

```nginx
server {
    listen 80;
    server_name admin.wu-jinsen.com;

    root /opt/moli/frontend/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff2?)$ {
        expires 7d;
        access_log off;
    }
}
```

### 9.3 API 站点（api）

```bash
sudo vi /etc/nginx/conf.d/moli-api.conf
```

```nginx
server {
    listen 80;
    server_name api.wu-jinsen.com;

    location / {
        proxy_pass http://127.0.0.1:8888;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 9.4 同域反代（可选，单域名简化 CORS）

若希望 `admin.wu-jinsen.com` 同时反代 API，在 admin 配置中追加：

```nginx
location ~ ^/(login|logout|captchaImage|system|operation|chatgpt) {
    proxy_pass http://127.0.0.1:8888;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

前端 `VUE_APP_BASE_API` 设为空字符串即可。

### 9.5 启动 Nginx

```bash
sudo nginx -t
sudo systemctl enable nginx
sudo systemctl restart nginx
```

---

## 10. 域名与 HTTPS

### 10.1 DNS（Route 53 或域名服务商）

| 记录类型 | 主机记录 | 值 |
|----------|----------|-----|
| A | `admin` | EC2 公网 IP |
| A | `api` | EC2 公网 IP |

### 10.2 免费 SSL（Certbot）

**Amazon Linux / Ubuntu：**

```bash
# Ubuntu
sudo apt install -y certbot python3-certbot-nginx

# 申请证书
sudo certbot --nginx -d admin.wu-jinsen.com -d api.wu-jinsen.com
```

按提示选择自动跳转 HTTPS。证书会自动续期。

---

## 11. 生产检查清单

- [ ] MySQL 仅 `127.0.0.1:3306` 监听
- [ ] Redis 仅 `127.0.0.1:6379` 监听，且 `redis6-cli ping`（AL2023）返回 PONG
- [ ] `spring.redis.database=0`
- [ ] 后端 `systemctl status moli-server` 为 active
- [ ] `curl` 本机 `/login` 成功
- [ ] 浏览器访问 `https://admin.wu-jinsen.com` 正常
- [ ] 浏览器访问 `https://api.wu-jinsen.com/login`（或前端登录）成功
- [ ] 生产配置无明文密码提交到 Git
- [ ] `swagger.show: false`（生产关闭文档）

---

## 12. 常见问题

### 12.1 能启动但登录失败

| 报错 | 原因 | 处理 |
|------|------|------|
| `Connection refused` | Redis/MySQL 未启动 | `systemctl start redis6 mysqld` |
| `ERR unknown command 'SELECT'` | 连了 Serverless Redis | 改用 EC2 自建 Redis |
| `ERR unknown command 'SCAN'` | 同上，或 shiro-redis 扫 Session | 改用标准 Redis；确保代码已避免 SCAN 枚举 |
| `Access denied for user` | MySQL 账号密码错误 | 核对 `application-pro.yml` / 环境变量 |
| `Public Key Retrieval is not allowed` | MySQL 8 默认 `caching_sha2_password`，JDBC 未允许取公钥 | JDBC URL 加 `allowPublicKeyRetrieval=true`（见 `application-pro.yml.example`），重启后端 |

### 12.2 前端刷新 404

Nginx 需配置：

```nginx
try_files $uri $uri/ /index.html;
```

### 12.3 接口跨域

- 推荐：`admin` + `api` 分域，后端 `CORSConfiguration` 已允许 `*`
- 更稳妥：生产将 `allowedOrigins` 收紧为 `https://admin.wu-jinsen.com`
- 或使用同域反代，无需跨域

### 12.4 导出接口文档给前端

开发环境开启 `swagger.show: true` 后：

```bash
curl http://localhost:8888/v2/api-docs -o swagger.json
```

生产环境建议关闭 Swagger，在测试环境导出后发给前端。

---

## 13. 后续扩展

| 阶段 | 建议 |
|------|------|
| 流量增大 | MySQL 迁 RDS，Redis 迁 ElastiCache **标准节点**（非 Serverless） |
| 静态资源 | 前端迁 S3 + CloudFront |
| 文件上传 | MinIO 换 S3 |
| 密钥管理 | AWS Secrets Manager 或 SSM Parameter Store |

---

## 14. 相关文档

- [项目迭代基线](project-iteration-baseline.md)
- [接口迭代地图](api-iteration-map.md)
- [English version](aws-deployment-guide.en.md)
