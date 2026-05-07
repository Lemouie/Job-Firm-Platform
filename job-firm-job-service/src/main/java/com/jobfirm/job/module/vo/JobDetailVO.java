package com.jobfirm.job.module.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 差事详情展示 VO
 * 用于返回差事详情页信息
 */
@Data
public class JobDetailVO {
    private Long id;                // 差事ID
    private String title;           // 差事标题
    private String description;     // 差事描述
    private String category;        // 差事分类
    private Boolean isVip;          // 是否VIP
    private BigDecimal price;       // 差事价格
    private Integer orderCount;     // 订单数量
    private String status;          // 差事状态
    private List<String> images;    // 差事图片（轮播）
    private String firmName;        // 事务所名称
    private String firmDescription; // 事务所简介
}
