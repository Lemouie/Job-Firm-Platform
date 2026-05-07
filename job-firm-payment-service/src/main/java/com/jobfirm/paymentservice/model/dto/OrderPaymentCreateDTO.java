package com.jobfirm.paymentservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建差事支付记录 DTO
 */
@Data
public class OrderPaymentCreateDTO {

    /** 差事订单ID */
    private Long orderId;

    /** 顾客ID */
    private Long customerId;

    /** 事务所ID */
    private Long firmId;

    /** 支付金额（单位：分） */
    private BigDecimal amount;

    /** 支付方式：ALIPAY / WECHAT / BANK */
    private String payMethod;
}
