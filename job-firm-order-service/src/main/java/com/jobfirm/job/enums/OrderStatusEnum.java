package com.jobfirm.job.enums;

/**
 * 订单状态枚举
 * 对应数据库字段 order.status
 */
public enum OrderStatusEnum {
    PENDING,      // 等待支付
    PAID,         // 已支付
    FAILED,       // 支付失败
    EXECUTING,    // 执行中
    EXECUTED,     // 已执行
    ACCEPTED,     // 已验收
    CANCELLED,    // 已取消
    ADJUDICATED   // 已裁决
}
