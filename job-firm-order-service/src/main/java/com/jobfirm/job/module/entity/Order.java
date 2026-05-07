package com.jobfirm.job.module.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobfirm.infrastructure.entity.BaseEntity;
import com.jobfirm.job.enums.OrderStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单表实体类
 * 对应表：order
 */
@Data
@TableName("`order`")
public class Order extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;                // 订单ID
    private Long jobId;             // 差事ID
    private Long customerId;        // 顾客ID
    private Long firmId;            // 事务所ID
    private BigDecimal amount;      // 订单金额
    private OrderStatusEnum status; // 订单状态
    private String cancelReason;    // 取消原因
}
