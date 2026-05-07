package com.jobfirm.paymentservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.paymentservice.enums.OrderPaymentStatusEnum;
import com.jobfirm.paymentservice.model.entity.OrderPayment;
import com.jobfirm.paymentservice.model.dto.OrderPaymentCallbackDTO;
import com.jobfirm.paymentservice.model.dto.OrderPaymentCreateDTO;
import com.jobfirm.paymentservice.model.vo.OrderPaymentVO;
import com.jobfirm.paymentservice.mapper.OrderPaymentMapper;
import com.jobfirm.paymentservice.service.OrderPaymentService;
/*----------------------------------*/
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

/**
 * 差事支付接收（托管）服务
 */
@Service
@RequiredArgsConstructor
public class OrderPaymentServiceImpl implements OrderPaymentService {
    private final OrderPaymentMapper orderPaymentMapper;

    @Override
    public OrderPaymentVO getOrderPayment(Long id) {
        OrderPayment payment = orderPaymentMapper.selectById(id);
        if (payment == null) return null;

        OrderPaymentVO vo = new OrderPaymentVO();
        BeanUtils.copyProperties(payment, vo);
        return vo;
    }

    @Override
    public Long createOrderPayment(OrderPaymentCreateDTO dto) {
        OrderPayment payment = new OrderPayment();
        payment.setOrderId(dto.getOrderId());
        payment.setCustomerId(dto.getCustomerId());
        payment.setFirmId(dto.getFirmId());
        payment.setAmount(dto.getAmount());
        payment.setPayMethod(dto.getPayMethod());
        payment.setStatus(OrderPaymentStatusEnum.PENDING);

        orderPaymentMapper.insert(payment);
        return payment.getId();
    }

    @Override
    public void handleOrderPaymentCallback(OrderPaymentCallbackDTO dto) {
        OrderPayment payment = orderPaymentMapper.selectById(dto.getPaymentId());
        if (payment == null) return;

        if (Boolean.TRUE.equals(dto.getSuccess())) {
            payment.setStatus(OrderPaymentStatusEnum.LOCKED);
            payment.setLockedAmount(payment.getAmount());
        } else {
            payment.setStatus(OrderPaymentStatusEnum.FAILED);
        }

        payment.setTransactionId(dto.getTransactionId());
        orderPaymentMapper.updateById(payment);
    }

    @Override
    public OrderPaymentVO getPaymentByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderPayment> wrapper = new LambdaQueryWrapper<OrderPayment>()
                .eq(OrderPayment::getOrderId, orderId)
                .orderByDesc(OrderPayment::getCreatedAt)
                .last("LIMIT 1");
        OrderPayment payment = orderPaymentMapper.selectOne(wrapper);
        if (payment == null) return null;

        OrderPaymentVO vo = new OrderPaymentVO();
        BeanUtils.copyProperties(payment, vo);
        return vo;
    }

    @Override
    public void refundPayment(Long orderPaymentId) {
        OrderPayment payment = orderPaymentMapper.selectById(orderPaymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        if (!OrderPaymentStatusEnum.LOCKED.equals(payment.getStatus())) {
            throw new BusinessException(ErrorCode.PAYMENT_STATUS_INVALID);
        }

        payment.setStatus(OrderPaymentStatusEnum.REFUNDED);
        payment.setRefundedAmount(payment.getLockedAmount() != null ? payment.getLockedAmount() : payment.getAmount());

        // TODO: 调用第三方支付平台执行退款
        // bankClient.refund(payment.getTransactionId(), payment.getRefundedAmount());

        orderPaymentMapper.updateById(payment);
    }

}
