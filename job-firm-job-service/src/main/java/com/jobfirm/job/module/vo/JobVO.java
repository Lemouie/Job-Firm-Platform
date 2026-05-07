package com.jobfirm.job.module.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 差事列表展示 VO
 * 用于返回差事缩略信息
 */
@Data
public class JobVO {
    private Long id;                // 差事ID
    private String title;           // 差事标题
    private String category;        // 差事分类
    private Boolean isVip;          // 是否VIP
    private BigDecimal price;       // 差事价格
    private Integer orderCount;     // 订单数量
}
