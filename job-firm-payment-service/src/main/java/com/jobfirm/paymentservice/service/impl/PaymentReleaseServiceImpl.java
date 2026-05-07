package com.jobfirm.paymentservice.service.impl;

import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.paymentservice.enums.OrderPaymentStatusEnum;
import com.jobfirm.paymentservice.mapper.OrderPaymentMapper;
import com.jobfirm.paymentservice.model.entity.OrderPayment;
import com.jobfirm.paymentservice.service.PaymentForwardService;
import com.jobfirm.paymentservice.service.PaymentReleaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 支付释放模块实现类（解除托管）
 * 功能：
 * - 完全释放托管资金
 * - 比例释放托管资金
 */
@Service
@RequiredArgsConstructor
public class PaymentReleaseServiceImpl implements PaymentReleaseService {

    private final OrderPaymentMapper orderPaymentMapper;
    private final PaymentForwardService paymentForwardService;

    @Override
    public void releaseFull(Long orderPaymentId) {

        // 1. 查询差事支付记录
        OrderPayment payment = orderPaymentMapper.selectById(orderPaymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        // 2. 必须是 LOCKED 状态才能释放
        if (!OrderPaymentStatusEnum.LOCKED.equals(payment.getStatus())) {
            throw new BusinessException(ErrorCode.PAYMENT_STATUS_INVALID);
        }

        BigDecimal lockedAmount = payment.getLockedAmount();

        // TODO：托管资金进入平台公共账户-银行卡（事务所收益池）
        // bankClient.transferToPlatformPool(lockedAmount);

        // 3. 更新支付记录状态为 RELEASED
        payment.setStatus(OrderPaymentStatusEnum.RELEASED);
        payment.setReleasedAmount(lockedAmount);
        orderPaymentMapper.updateById(payment);

        // 4. 转发给事务所收益（记账）
        paymentForwardService.forwardToFirmWallet(orderPaymentId);
    }

    @Override
    public void releasePartial(Long orderPaymentId) {

        // 1. 查询差事支付记录
        OrderPayment payment = orderPaymentMapper.selectById(orderPaymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        // 2. 必须是 LOCKED 状态才能释放
        if (!OrderPaymentStatusEnum.LOCKED.equals(payment.getStatus())) {
            throw new BusinessException(ErrorCode.PAYMENT_STATUS_INVALID);
        }

        BigDecimal lockedAmount = payment.getLockedAmount();
        BigDecimal refundAmount = payment.getRefundedAmount();
        BigDecimal firmAmount = payment.getReleasedAmount();

        // 3. 校验金额合法性：refundedAmount + releasedAmount 不能超过 lockedAmount
        if (refundAmount == null) refundAmount = BigDecimal.ZERO;
        if (firmAmount == null) firmAmount = BigDecimal.ZERO;
        if (refundAmount.add(firmAmount).compareTo(lockedAmount) > 0) {
            throw new BusinessException(ErrorCode.PAYMENT_STATUS_INVALID);
        }

        // 4. 若 releasedAmount 未设置，默认为 lockedAmount - refundAmount
        if (firmAmount.compareTo(BigDecimal.ZERO) == 0) {
            firmAmount = lockedAmount.subtract(refundAmount);
        }

        // TODO：退给顾客的金额（银行卡原路退回）
        // bankClient.refundToCustomer(refundAmount);

        // TODO：平台公共账户-银行卡收入（收益池）
        // bankClient.transferToPlatformPool(firmAmount);

        // 5. 更新支付记录状态
        payment.setStatus(OrderPaymentStatusEnum.PARTIAL_RELEASED);
        payment.setReleasedAmount(firmAmount);
        payment.setRefundedAmount(refundAmount);
        orderPaymentMapper.updateById(payment);

        // 6. 转发给事务所收益（记账）
        paymentForwardService.forwardToFirmWallet(orderPaymentId);
    }
}
