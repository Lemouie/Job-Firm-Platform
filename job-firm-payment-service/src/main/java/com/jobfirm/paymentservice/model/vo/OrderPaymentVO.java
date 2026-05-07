package com.jobfirm.paymentservice.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 差事支付记录 VO
 */
@Data
public class OrderPaymentVO {

    private Long id;

    private Long orderId;

    private Long customerId;

    private Long firmId;

    private BigDecimal amount;

    private String payMethod;

    private String status;

    private String transactionId;

    private String createdAt;
}
