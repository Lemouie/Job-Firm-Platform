package com.jobfirm.paymentservice.model.dto;

import lombok.Data;

/**
 * VIP 支付结果回调 DTO
 */
@Data
public class VipPaymentCallbackDTO {

    /** 支付记录ID */
    private Long paymentId;

    /** 第三方支付流水号 */
    private String transactionId;

    /** 支付是否成功：true/false */
    private Boolean success;
}
