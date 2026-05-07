-- ============================================
-- Job-Firm Management 数据库初始化脚本
-- 合并: Admin + Archive + Recommend (port 8088)
-- ============================================

CREATE DATABASE IF NOT EXISTS job_firm_management
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE job_firm_management;

-- ========================
-- 管理员操作日志表
-- ========================
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

-- ========================
-- 归档记录表
-- ========================
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
