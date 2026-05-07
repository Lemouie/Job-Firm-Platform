package com.jobfirm.paymentservice.service;

/**
 * 支付释放服务（解除托管）
 * 功能：
 * - 完全释放托管资金
 * - 比例释放托管资金
 */
public interface PaymentReleaseService {

    /**
     * 完全释放托管资金
     * @param orderPaymentId 订单支付记录ID
     */
    void releaseFull(Long orderPaymentId);

    /**
     * 比例释放托管资金
     * @param orderPaymentId 订单支付记录ID
     */
    void releasePartial(Long orderPaymentId);
}
