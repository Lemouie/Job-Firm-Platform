package com.jobfirm.paymentservice.service;

import java.math.BigDecimal;

/**
 * 支付提现服务
 * 功能：提现事务所收益
 */
public interface PaymentWithdrawService {

    /**
     * 提现事务所收益
     * @param firmId 事务所ID
     * @param amount 提现金额（单位：分）
     */
    void withdrawFirmRevenue(Long firmId, BigDecimal amount);
}
