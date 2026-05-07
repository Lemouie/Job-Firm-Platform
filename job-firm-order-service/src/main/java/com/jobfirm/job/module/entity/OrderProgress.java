package com.jobfirm.job.module.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobfirm.infrastructure.entity.BaseEntity;
import com.jobfirm.job.enums.OrderProgressEnum;
import lombok.Data;

/**
 * 订单进度表实体类
 * 对应表：order_progress
 */
@Data
@TableName("order_progress")
public class OrderProgress extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;                   // 进度ID
    private Long orderId;              // 订单ID
    private OrderProgressEnum progress;// 订单执行进度阶段（默认 CREATED）
    private String progressDesc;       // 进度描述，补充说明执行情况
    private String imageUrl1;          // 图片1
    private String imageUrl2;          // 图片2
    private String imageUrl3;          // 图片3
    private String imageUrl4;          // 图片4
    private String imageUrl5;          // 图片5
    private String imageUrl6;          // 图片6
    private String imageUrl7;          // 图片7
    private String imageUrl8;          // 图片8
    private String imageUrl9;          // 图片9
}
