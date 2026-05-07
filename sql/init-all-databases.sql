-- ============================================
-- Job-Firm Platform 数据库初始化脚本
-- 基于 Nacos + Spring Cloud Gateway + RocketMQ
-- ============================================

-- ========================
-- Nacos 配置中心数据库
-- ========================
CREATE DATABASE IF NOT EXISTS nacos_config
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS job_firm_user
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE job_firm_user;

CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT       NOT NULL COMMENT '用户ID，雪花算法',
  `username`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `phone`       VARCHAR(20)  NOT NULL COMMENT '手机号',
  `email`       VARCHAR(100) NOT NULL COMMENT '邮箱',
  `password`    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `avatar_url`  VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  `role`        ENUM('CUSTOMER','CEO','ADMIN') NOT NULL DEFAULT 'CUSTOMER' COMMENT '角色',
  `status`      ENUM('ACTIVE','DISABLED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========================
-- 2. 事务所服务库
-- ========================
CREATE DATABASE IF NOT EXISTS job_firm_firm
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE job_firm_firm;

CREATE TABLE IF NOT EXISTS firm (
  `id`              BIGINT       NOT NULL COMMENT '事务所ID',
  `ceo_id`          BIGINT       NOT NULL COMMENT 'CEO用户ID',
  `name`            VARCHAR(100) NOT NULL COMMENT '事务所名称',
  `description`     TEXT         DEFAULT NULL COMMENT '事务所简介',
  `logo_url`        VARCHAR(255) DEFAULT NULL COMMENT 'Logo',
  `escape_count`    TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逃单次数(0-5)',
  `revenue`         DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '平台钱包收益(可提现)',
  `status`          ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `vip_status`      ENUM('NONE','ACTIVE','EXPIRED') NOT NULL DEFAULT 'NONE' COMMENT 'VIP状态',
  `vip_expire_time` DATETIME     DEFAULT NULL COMMENT 'VIP到期时间',
  `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ceo_id` (`ceo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事务所表';

-- ========================
-- 3. 差事服务库
-- ========================
CREATE DATABASE IF NOT EXISTS job_firm_job
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE job_firm_job;

CREATE TABLE IF NOT EXISTS job (
  `id`          BIGINT        NOT NULL COMMENT '差事ID',
  `firm_id`     BIGINT        NOT NULL COMMENT '事务所ID',
  `title`       VARCHAR(100)  NOT NULL COMMENT '差事标题',
  `description` TEXT          DEFAULT NULL COMMENT '差事描述',
  `price`       DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '差事价格',
  `category`    ENUM('LIFE','SKILL','ENTERTAINMENT') NOT NULL DEFAULT 'LIFE' COMMENT '分类',
  `is_vip`      TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '是否VIP差事',
  `status`      ENUM('PENDING','APPROVED','REJECTED','PUBLISHED','UNPUBLISHED') NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `order_count` INT UNSIGNED  NOT NULL DEFAULT 0 COMMENT '被下单次数(用于推荐排序)',
  `created_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_firm_id` (`firm_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='差事表';

CREATE TABLE IF NOT EXISTS job_image (
  `id`          BIGINT       NOT NULL COMMENT '图片ID',
  `job_id`      BIGINT       NOT NULL COMMENT '差事ID',
  `image_url`   VARCHAR(500) NOT NULL COMMENT '图片URL',
  `sort_order`  TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序(0-8)',
  `created_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_job_id` (`job_id`),
  CONSTRAINT `fk_job_image_job` FOREIGN KEY (`job_id`) REFERENCES job(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='差事图片表';

-- ========================
-- 4. 订单服务库
-- ========================
CREATE DATABASE IF NOT EXISTS job_firm_order
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE job_firm_order;

CREATE TABLE IF NOT EXISTS `order` (
  `id`          BIGINT        NOT NULL COMMENT '订单ID',
  `job_id`      BIGINT        NOT NULL COMMENT '差事ID',
  `customer_id` BIGINT        NOT NULL COMMENT '顾客ID',
  `firm_id`     BIGINT        NOT NULL COMMENT '事务所ID',
  `amount`      DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `status`      ENUM('PENDING','PAID','FAILED','EXECUTING','EXECUTED','ACCEPTED','CANCELLED','ADJUDICATED')
                NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
  `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
  `created_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_firm` (`firm_id`),
  KEY `idx_job` (`job_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS order_progress (
  `id`            BIGINT       NOT NULL COMMENT '进度ID',
  `order_id`      BIGINT       NOT NULL COMMENT '订单ID',
  `progress`      ENUM('CREATED','STARTED','IN_PROGRESS','COMPLETED','VERIFIED','REJECTED')
                  NOT NULL DEFAULT 'CREATED' COMMENT '执行进度阶段',
  `progress_desc` VARCHAR(255) DEFAULT NULL COMMENT '进度描述',
  `image_urls`    TEXT         DEFAULT NULL COMMENT '进度图片(JSON数组)',
  `created_at`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  CONSTRAINT `fk_progress_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单进度表';

-- ========================
-- 5. 支付服务库
-- ========================
CREATE DATABASE IF NOT EXISTS job_firm_payment
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE job_firm_payment;

-- VIP支付记录
CREATE TABLE IF NOT EXISTS payment_vip (
  `id`             BIGINT        NOT NULL COMMENT '主键',
  `firm_id`        BIGINT        NOT NULL COMMENT '事务所ID',
  `amount`         DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  `pay_method`     ENUM('ALIPAY','WECHAT','BANK') NOT NULL DEFAULT 'ALIPAY' COMMENT '支付方式',
  `status`         ENUM('PENDING','SUCCESS','FAILED') NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `vip_type`       ENUM('MONTHLY','YEARLY') NOT NULL DEFAULT 'MONTHLY' COMMENT 'VIP类型',
  `transaction_id` VARCHAR(64)   DEFAULT NULL COMMENT '第三方流水号',
  `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VIP支付记录表';

-- 订单支付记录（托管支付）
CREATE TABLE IF NOT EXISTS payment_order (
  `id`              BIGINT        NOT NULL COMMENT '主键',
  `order_id`        BIGINT        NOT NULL COMMENT '订单ID',
  `customer_id`     BIGINT        NOT NULL COMMENT '顾客ID',
  `firm_id`         BIGINT        NOT NULL COMMENT '事务所ID',
  `amount`          DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  `pay_method`      ENUM('ALIPAY','WECHAT','BANK') NOT NULL DEFAULT 'ALIPAY' COMMENT '支付方式',
  `status`          ENUM('PENDING','LOCKED','FAILED','RELEASED','REFUNDED','PARTIAL_RELEASED')
                    NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `locked_amount`   DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '锁定托管金额',
  `released_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '已释放金额',
  `refunded_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '已退款金额',
  `transaction_id`  VARCHAR(64)   DEFAULT NULL COMMENT '第三方流水号',
  `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`),
  KEY `idx_firm` (`firm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付记录表(托管)';

-- ========================
-- 6. 归档服务库
-- ========================
CREATE DATABASE IF NOT EXISTS job_firm_archive
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE job_firm_archive;

CREATE TABLE IF NOT EXISTS archive_record (
  `id`            BIGINT       NOT NULL COMMENT '归档批次ID',
  `archive_type`  ENUM('ORDER','PAYMENT') NOT NULL COMMENT '归档类型',
  `archive_month` VARCHAR(7)   NOT NULL COMMENT '归档月份(yyyy-MM)',
  `oss_path`      VARCHAR(500) NOT NULL COMMENT 'OSS文件路径',
  `record_count`  INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据条数',
  `status`        ENUM('RUNNING','SUCCESS','FAILED') NOT NULL DEFAULT 'RUNNING' COMMENT '执行状态',
  `start_time`    DATETIME     DEFAULT NULL COMMENT '归档开始时间',
  `end_time`      DATETIME     DEFAULT NULL COMMENT '归档结束时间',
  `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_type_month` (`archive_type`, `archive_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='归档记录表';

-- ========================
-- 7. 后台管理服务库
-- ========================
CREATE DATABASE IF NOT EXISTS job_firm_admin
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE job_firm_admin;

CREATE TABLE IF NOT EXISTS admin_action_log (
  `id`          BIGINT       NOT NULL COMMENT '主键',
  `admin_id`    BIGINT       NOT NULL COMMENT '管理员ID',
  `action`      VARCHAR(100) NOT NULL COMMENT '操作类型',
  `target_type` VARCHAR(50)  DEFAULT NULL COMMENT '操作对象类型',
  `target_id`   BIGINT       DEFAULT NULL COMMENT '操作对象ID',
  `detail`      TEXT         DEFAULT NULL COMMENT '操作详情',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_admin` (`admin_id`),
  KEY `idx_action` (`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作日志表';

-- 默认管理员（密码: admin123, BCrypt加密）
INSERT IGNORE INTO job_firm_user.`user` (id, username, phone, email, password, role, status)
VALUES (1, '管理员', '13800000000', 'admin@jobfirm.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ADMIN', 'ACTIVE');
