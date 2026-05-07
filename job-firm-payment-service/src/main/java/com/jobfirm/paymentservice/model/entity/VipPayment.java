package com.jobfirm.paymentservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobfirm.infrastructure.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 平台 VIP 支付记录实体
 * 对应表：payment_vip
 *
 * 功能覆盖：
 * - 创建VIP支付记录
 * - 确认支付方式（支付宝/微信/银行卡）
 * - 接受VIP支付（款项进入平台收益账户-银行卡）
 * - VIP支付失败
 */
@Data
@TableName("payment_vip")
public class VipPayment extends BaseEntity {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 事务所ID */
    private Long firmId;

    /** 支付金额（单位：分） */
    private BigDecimal amount;

    /** 支付方式：ALIPAY / WECHAT / BANK */
    private String payMethod;

    /** 支付状态：PENDING / SUCCESS / FAILED */
    private String status;

    /** 第三方支付流水号 */
    private String transactionId;
}
