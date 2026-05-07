package com.jobfirm.job.module.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobfirm.infrastructure.entity.BaseEntity;
import com.jobfirm.job.enums.CategoryEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 差事表实体类
 * 对应表：job
 */
@Data
@TableName("job")
public class Job extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;                // 主键ID，雪花算法生成

    private Long firmId;            // 事务所ID（关联 firm-service）

    private String title;           // 差事标题

    private String description;     // 差事描述（默认NULL）

    private CategoryEnum category;  // 差事主题分类（默认LIFE）

    private Boolean isVip;          // 是否VIP差事（NOT NULL，默认0）

    private BigDecimal price;       // 差事价格（默认0）

    private Integer orderCount;     // 订单数量（默认0）

    private String status;          // 差事状态：PENDING/APPROVED/REJECTED/PUBLISHED/UNPUBLISHED
}
