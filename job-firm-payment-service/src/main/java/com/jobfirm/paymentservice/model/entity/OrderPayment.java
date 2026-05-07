package com.jobfirm.paymentservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobfirm.infrastructure.entity.BaseEntity;
import com.jobfirm.paymentservice.enums.OrderPaymentStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 差事支付记录（托管）实体
 * 对应表：payment_order
 *
 * 功能覆盖：
 * - 创建支付记录
 * - 确认支付方式（支付宝/微信/银行卡）
 * - 锁定支付（托管账户-银行卡）
 * - 支付失败
 * - 支付释放（完全/比例）
 * - 支付转发（记账到事务所收益字段）
 * - 支付提现
 * - 支付统计（昨日/月度/年度/累计）
 */
@Data
@TableName("payment_order")
public class OrderPayment extends BaseEntity {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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

    /** 状态：PENDING / LOCKED / FAILED / RELEASED / REFUNDED / PARTIAL_RELEASED */
    private OrderPaymentStatusEnum status;

    /** 锁定金额（托管金额） */
    private BigDecimal lockedAmount;

    /** 已释放金额 */
    private BigDecimal releasedAmount;

    /** 已退款金额（比例释放时退给顾客） */
    private BigDecimal refundedAmount;

    /** 第三方支付流水号 */
    private String transactionId;
}
