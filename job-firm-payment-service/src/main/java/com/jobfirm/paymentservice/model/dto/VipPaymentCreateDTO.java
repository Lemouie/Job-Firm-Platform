package com.jobfirm.paymentservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建 VIP 支付记录 DTO
 */
@Data
public class VipPaymentCreateDTO {

    /** 事务所ID */
    private Long firmId;

    /** 支付金额（单位：分） */
    private BigDecimal amount;

    /** 支付方式：ALIPAY / WECHAT / BANK */
    private String payMethod;
}
