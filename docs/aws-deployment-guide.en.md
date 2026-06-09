# AWS Deployment Guide (MySQL + Nginx + Redis)

For **moli-project-single** (Tangyu admin backend) with a separate Vue frontend.

This guide recommends a **single EC2** with self-hosted MySQL, Redis, and Nginx. It is cost-effective and compatible with Shiro + Jedis. Scale out to RDS and standard ElastiCache later if needed.

---

## 1. Architecture

```
Internet → Nginx (:80/:443)
             ├─ admin.example.com → /opt/moli/frontend/dist
             └─ api.example.com   → 127.0.0.1:8888 (Spring Boot)

Java (:8888) → MySQL (:3306, localhost)
            → Redis (:6379, localhost)
```

| Component | Recommendation |
|-----------|----------------|
| Frontend | Nginx static files at `/opt/moli/frontend/dist` |
| Backend | EC2 + systemd, JAR at `/opt/moli/backend` |
| MySQL | EC2 self-hosted or RDS |
| Redis | **EC2 self-hosted** — **do not use ElastiCache Serverless** (no SELECT/SCAN; breaks Shiro login) |

---

## 2. Prerequisites

- AWS account (e.g. `ap-southeast-1` or `ap-southeast-2`)
- Domain name and SSH key pair (`.pem`)
- Local builds: `mvn package` (backend), `npm run build` (frontend)

---

## 3. Create EC2

| Setting | Suggestion |
|---------|------------|
| AMI | Amazon Linux 2023 or Ubuntu 22.04 |
| Type | `t3.small` |
| Storage | 30 GB gp3 |

**Security group inbound:**

| Port | Source | Purpose |
|------|--------|---------|
| 22 | Your IP | SSH |
| 80 | 0.0.0.0/0 | HTTP |
| 443 | 0.0.0.0/0 | HTTPS |

Do **not** expose 3306, 6379, or 8888 to the public internet.

---

## 4. Install packages

**Amazon Linux 2023:**

```bash
sudo dnf update -y
sudo dnf install -y java-11-amazon-corretto-headless nginx redis6 mysql-server
```

**Ubuntu 22.04:**

```bash
sudo apt update -y
sudo apt install -y openjdk-11-jdk nginx redis-server mysql-server
```

**Directories:**

```bash
sudo mkdir -p /opt/moli/{frontend/dist,backend,logs,sql}
sudo chown -R $USER:$USER /opt/moli
```

---

## 5. MySQL

```bash
sudo systemctl enable mysqld   # or mysql on Ubuntu
sudo systemctl start mysqld
sudo mysql_secure_installation
```

```sql
CREATE DATABASE moli DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER 'moli'@'localhost' IDENTIFIED BY 'your-strong-password';
GRANT ALL PRIVILEGES ON moli.* TO 'moli'@'localhost';
FLUSH PRIVILEGES;
```

Import schema:

```bash
mysql -u moli -p moli < /opt/moli/sql/schema_moli.sql
mysql -u moli -p moli < /opt/moli/sql/seed_sys_menu.sql
```

Ensure MySQL listens on `127.0.0.1:3306` only.

---

## 6. Redis

> **Important:** This project requires standard Redis commands (`SELECT`, `SCAN`).  
> **Do not use ElastiCache Serverless.**

Edit `/etc/redis6/redis6.conf` (or `/etc/redis/redis.conf`):

```conf
bind 127.0.0.1
requirepass your-redis-password
```

```bash
sudo systemctl enable redis6
sudo systemctl restart redis6
redis-cli -a your-redis-password ping   # expect PONG
```

Use `spring.redis.database: 0`.

---

## 7. Backend (Spring Boot)

Build locally:

```bash
cd moli-parent && mvn -DskipTests install && cd ..
mvn -pl moli-common,moli-server -am -DskipTests package
```

Upload JAR to `/opt/moli/backend/moli-server.jar`.

**Environment variables (recommended):**

```bash
export SPRING_PROFILES_ACTIVE=pro
export DB_HOST=127.0.0.1
export SPRING_DATASOURCE_USERNAME=moli
export SPRING_DATASOURCE_PASSWORD=your-mysql-password
export SPRING_REDIS_HOST=127.0.0.1
export SPRING_REDIS_PASSWORD=your-redis-password
export SPRING_REDIS_DATABASE=0
```

**systemd** (`/etc/systemd/system/moli-server.service`):

```ini
[Unit]
Description=Moli Server
After=network.target mysqld.service redis6.service

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/opt/moli/backend
Environment=SPRING_PROFILES_ACTIVE=pro
Environment=DB_HOST=127.0.0.1
Environment=SPRING_DATASOURCE_USERNAME=moli
Environment=SPRING_DATASOURCE_PASSWORD=your-mysql-password
Environment=SPRING_REDIS_HOST=127.0.0.1
Environment=SPRING_REDIS_PASSWORD=your-redis-password
Environment=SPRING_REDIS_DATABASE=0
ExecStart=/usr/bin/java -jar /opt/moli/backend/moli-server.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now moli-server
```

Verify:

```bash
curl -s http://127.0.0.1:8888/login \
  -H "Content-Type: application/json" \
  -d '{"userName":"admin","password":"your-password"}'
```

---

## 8. Nginx

Upload frontend build:

```bash
rsync -avz --delete -e "ssh -i key.pem" dist/ ec2-user@IP:/opt/moli/frontend/dist/
```

**Admin site** (`/etc/nginx/conf.d/moli-admin.conf`):

```nginx
server {
    listen 80;
    server_name admin.example.com;
    root /opt/moli/frontend/dist;
    index index.html;
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

**API site** (`/etc/nginx/conf.d/moli-api.conf`):

```nginx
server {
    listen 80;
    server_name api.example.com;
    location / {
        proxy_pass http://127.0.0.1:8888;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
sudo nginx -t && sudo systemctl restart nginx
```

---

## 9. DNS & HTTPS

| Type | Host | Value |
|------|------|-------|
| A | admin | EC2 public IP |
| A | api | EC2 public IP |

```bash
sudo certbot --nginx -d admin.example.com -d api.example.com
```

---

## 10. Troubleshooting

| Error | Fix |
|-------|-----|
| App starts, login fails | Redis/MySQL not running or wrong credentials |
| `ERR unknown command 'SELECT'` | Replace Serverless Redis with standard Redis on EC2 |
| `ERR unknown command 'SCAN'` | Same as above |
| Frontend refresh 404 | Add `try_files $uri $uri/ /index.html;` |

---

## 11. Related docs

- [Project baseline](project-iteration-baseline.md)
- [API iteration map](api-iteration-map.md)
- [中文版](aws-deployment-guide.md)
