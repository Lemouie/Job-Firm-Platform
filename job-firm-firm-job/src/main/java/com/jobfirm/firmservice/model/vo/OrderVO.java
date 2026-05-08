package com.jobfirm.firmservice.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 订单展示对象
 */
@Data
public class OrderVO {
    private Long orderId;
    private Long firmId;
    private String status;        // 订单状态
    private String customerName;  // 客户昵称
    private String contactInfo;   // 事务所联系方式（如微信）
    private Date createdAt;
    private Date updatedAt;
}
