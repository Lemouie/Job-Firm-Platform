-- ============================================
-- Job-Firm Core 数据库初始化脚本
-- 合并: Auth + User (port 8081)
-- ============================================

CREATE DATABASE IF NOT EXISTS job_firm_core
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE job_firm_core;

-- ========================
-- 用户表
-- ========================
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

-- 默认管理员（密码: admin123, BCrypt加密）
INSERT IGNORE INTO `user` (id, username, phone, email, password, role, status)
VALUES (1, '管理员', '13800000000', 'admin@jobfirm.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ADMIN', 'ACTIVE');
