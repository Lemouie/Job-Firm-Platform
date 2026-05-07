# Job-Firm Platform — 差事事务所分布式微服务平台

[![Java 17](https://img.shields.io/badge/Java-17-blue)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-blueviolet)](https://spring.io/projects/spring-cloud)
[![Nacos](https://img.shields.io/badge/Nacos-2.3.2-orange)](https://nacos.io/)
[![RocketMQ](https://img.shields.io/badge/RocketMQ-5.1.4-red)](https://rocketmq.apache.org/)
[![Sentinel](https://img.shields.io/badge/Sentinel-1.8.7-green)](https://sentinelguard.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.2-DC382D)](https://redis.io/)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.5-red)](https://baomidou.com/)

基于 **Nacos + Spring Cloud Gateway + RocketMQ + Sentinel** 的分布式高并发差事事务所平台。涵盖用户、事务所、差事、订单、支付等核心微服务，实现交易闭环与自动化交付。

---

## 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                   Spring Cloud Gateway (8080)                │
│               JWT 鉴权 · 限流 · 路由转发 · Sentinel          │
└──────┬─────────┬─────────┬──────────┬──────────┬───────────┘
       │         │         │          │          │
       ▼         ▼         ▼          ▼          ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────────┐
│  Auth  │ │  User  │ │  Firm  │ │  Job   │ │   Order    │
│  (8081)│ │ (8082) │ │ (8083) │ │ (8084) │ │   (8085)   │
└───┬────┘ └───┬────┘ └───┬────┘ └───┬────┘ └─────┬──────┘
    │         │         │          │          │
    ▼         ▼         ▼          ▼          ▼
┌────────┐ ┌────────────────┐ ┌────────┐ ┌────────────┐
│Payment │ │   Recommend/   │ │ Admin  │ │  Archive   │
│ (8086) │ │   Search(8087) │ │ (8088) │ │   (8089)   │
└───┬────┘ └────────────────┘ └───┬────┘ └─────┬──────┘
    │                             │            │
    └───────────── Nacos + RocketMQ ──────────┘
                         │
              ┌──────────┴──────────┐
              ▼                     ▼
          MySQL 8.0              Redis 7.2
           (5 数据库)            (缓存/会话)
```

## 微服务列表

| 服务 | 端口 | 说明 | 数据库 |
|------|------|------|--------|
| `job-firm-gateway` | 8080 | 统一网关、JWT鉴权、限流 | - |
| `job-firm-auth` | 8081 | JWT令牌颁发/校验/刷新 | - |
| `job-firm-user-service` | 8082 | 用户注册/登录/资料管理 | `job_firm_user` |
| `job-firm-firm-service` | 8083 | 事务所创建/审核/VIP/收益 | `job_firm_firm` |
| `job-firm-job-service` | 8084 | 差事CRUD/图片/上下架 | `job_firm_job` |
| `job-firm-order-service` | 8085 | 订单状态机/进度管理 | `job_firm_order` |
| `job-firm-payment-service` | 8086 | 托管支付/释放/转发/提现/统计 | `job_firm_payment` |
| `job-firm-recommend-search-service` | 8087 | 推荐搜索 | - |
| `job-firm-admin-service` | 8088 | 后台管理 | `job_firm_admin` |
| `job-firm-archive-service` | 8089 | 归档 | `job_firm_archive` |

**基础模块：** `job-firm-common`（公共工具）、`job-firm-infrastructure`（基础设施配置）、`job-firm-api`（Feign API）

## 订单全状态机

```
PENDING ──pay()──▶ PAID ──execute()──▶ EXECUTING ──complete()──▶ EXECUTED ──accept()──▶ ACCEPTED
   │                 │                     │                          │
   └──cancel()──▶    │                     ├──cancel(firm)──▶ CANCELLED (refund + escape_count++)
   CANCELLED (refund)│                     │                          │
                     └──cancel()──▶        │                          └──dispute()──▶ ADJUDICATED
                     CANCELLED (refund)    └──reject(firm)──▶ CANCELLED (refund)
```

## 本地开发环境搭建

### 前置要求

- **JDK 17+** ([Eclipse Temurin](https://adoptium.net/) 推荐)
- **Maven 3.9+**
- **Docker Desktop** (用于 MySQL + Redis + Nacos + RocketMQ)

### 快速启动

```bash
# 1. 克隆项目
git clone https://github.com/Lemouie/Job-Firm-Platform.git
cd Job-Firm-Platform

# 2. 启动基础设施（MySQL + Redis + Nacos + RocketMQ）
docker compose -f docker-compose.env.yml up -d

# 3. 初始化数据库（自动执行 SQL）
# Docker 会自动执行 sql/init-all-databases.sql

# 4. 等待 Nacos 就绪（约 30 秒）
# 访问 http://localhost:8848/nacos （默认账号 nacos/nacos）

# 5. 编译所有模块（跳过测试）
mvn clean package -DskipTests

# 6. 按顺序启动服务
# 先启动 gateway 和 auth
java -jar job-firm-gateway/target/job-firm-gateway-1.0.0.jar &
java -jar job-firm-auth/target/job-firm-auth-1.0.0.jar &

# 再启动业务服务
java -jar job-firm-user-service/target/job-firm-user-service-1.0.0.jar &
java -jar job-firm-firm-service/target/job-firm-firm-service-1.0.0.jar &
# ... 其他服务同理
```

### 一键 Docker 部署

```bash
# 构建并启动所有服务
chmod +x scripts/startup.sh
./scripts/startup.sh
```

### 访问地址

| 服务 | 地址 |
|------|------|
| API 网关 | http://localhost:8080 |
| Nacos 控制台 | http://localhost:8848/nacos |
| MySQL | localhost:3306 (root/root123) |
| Redis | localhost:6379 |

### API 测试

```bash
# 1. 注册用户
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","email":"test@test.com","password":"test123"}'

# 2. 登录获取 Token
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"test123"}'

# 3. 获取用户信息（替换 YOUR_TOKEN）
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 技术栈

| 类别 | 技术 |
|------|------|
| **框架** | Spring Boot 3.2.3, Spring Cloud 2023.0.1 |
| **注册中心** | Alibaba Nacos 2.3.2 |
| **网关** | Spring Cloud Gateway |
| **消息队列** | Apache RocketMQ 5.1.4 |
| **限流熔断** | Alibaba Sentinel |
| **认证** | Spring Security + JWT |
| **ORM** | MyBatis-Plus 3.5.5 |
| **数据库** | MySQL 8.0 |
| **缓存** | Redis 7.2 |
| **服务调用** | OpenFeign + LoadBalancer |
| **部署** | Docker / Docker Compose |

## 项目结构

```
job-firm-platform/
├── job-firm-gateway/         # 网关服务
├── job-firm-auth/            # 认证服务
├── job-firm-user-service/    # 用户服务
├── job-firm-firm-service/    # 事务所服务
├── job-firm-job-service/     # 差事服务
├── job-firm-order-service/   # 订单服务
├── job-firm-payment-service/ # 支付服务
├── job-firm-recommend-search-service/  # 推荐搜索服务
├── job-firm-admin-service/   # 后台管理服务
├── job-firm-archive-service/ # 归档服务
├── job-firm-common/          # 公共模块
├── job-firm-infrastructure/  # 基础设施
├── job-firm-api/             # Feign接口
├── sql/                      # 数据库初始化脚本
├── scripts/                  # 运维脚本
├── docker-compose.yml        # 完整部署
├── docker-compose.env.yml    # 基础设施部署
└── Dockerfile                # 多阶段构建
```
