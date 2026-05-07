package com.jobfirm.paymentservice.enums;

/**
 * 订单支付状态枚举
 * 用于标识订单支付记录的阶段
 */
public enum OrderPaymentStatusEnum {
    PENDING,            // 待支付
    LOCKED,             // 支付成功 → 平台托管账户-银行卡
    FAILED,             // 支付失败
    RELEASED,           // 完全释放到平台公共账户-银行卡（事务所收益池）并转发到事务所收益
    REFUNDED,           // 完全退款给客户
    PARTIAL_RELEASED    // 比例释放（部分退顾客 + 部分入收益池 + 部分转发事务所）
}
