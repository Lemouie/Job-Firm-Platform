package com.jobfirm.paymentservice.service;

/**
 * 支付转发服务
 * 功能：将托管资金转发到事务所钱包（记账，不是真实打款）
 */
public interface PaymentForwardService {

    /**
     * 将托管金额转发到事务所钱包（记账）
     * @param orderPaymentId 差事支付记录ID
     */
    void forwardToFirmWallet(Long orderPaymentId);
}
