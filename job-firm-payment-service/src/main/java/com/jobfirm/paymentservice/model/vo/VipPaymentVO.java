package com.jobfirm.paymentservice.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * VIP 支付记录 VO
 */
@Data
public class VipPaymentVO {

    private Long id;

    private Long firmId;

    private BigDecimal amount;

    private String payMethod;

    private String status;

    private String transactionId;

    private String createdAt;
}
