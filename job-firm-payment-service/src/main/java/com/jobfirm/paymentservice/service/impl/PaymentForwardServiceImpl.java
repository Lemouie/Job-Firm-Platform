package com.jobfirm.paymentservice.service.impl;

import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.paymentservice.enums.OrderPaymentStatusEnum;
import com.jobfirm.paymentservice.mapper.OrderPaymentMapper;
import com.jobfirm.paymentservice.model.entity.OrderPayment;
import com.jobfirm.paymentservice.service.PaymentForwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 支付转发服务
 * 功能：将托管资金转发到事务所钱包（记账，不是真实打款）
 */
@Service
@RequiredArgsConstructor
public class PaymentForwardServiceImpl implements PaymentForwardService {
    private final OrderPaymentMapper orderPaymentMapper;

    @Override
    public void forwardToFirmWallet(Long orderPaymentId) {

        // 1. 查询差事支付记录
        OrderPayment payment = orderPaymentMapper.selectById(orderPaymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        // 2. 必须是已释放状态（RELEASED 或 PARTIAL_RELEASED）才允许转发
        if (!OrderPaymentStatusEnum.RELEASED.equals(payment.getStatus())
                && !OrderPaymentStatusEnum.PARTIAL_RELEASED.equals(payment.getStatus())) {
            throw new BusinessException(ErrorCode.PAYMENT_STATUS_INVALID);
        }

        BigDecimal amount = payment.getReleasedAmount();
        Long firmId = payment.getFirmId();

        // 3. TODO：调用事务所服务，增加事务所钱包字段
        // firmClient.addRevenue(firmId, amount);

    }
}
