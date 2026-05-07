package com.jobfirm.job.enums;

/**
 * 订单进度枚举
 * 用于标识订单执行过程中的阶段
 */
public enum OrderProgressEnum {
    CREATED,      // 已创建
    STARTED,      // 已开始执行
    IN_PROGRESS,  // 执行中
    COMPLETED,    // 已完成
    VERIFIED,     // 已验收
    REJECTED      //执行被拒收
}
