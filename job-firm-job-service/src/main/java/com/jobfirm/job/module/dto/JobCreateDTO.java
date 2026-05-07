package com.jobfirm.job.module.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 差事创建请求 DTO
 * 用于接收前端创建差事的入参
 */
@Data
public class JobCreateDTO {
    private Long firmId;            // 事务所ID
    private String title;           // 差事标题
    private String description;     // 差事描述
    private String category;        // 差事主题分类
    private Boolean isVip;          // 是否VIP差事
    private BigDecimal price;       // 差事价格
    private List<String> images;    // 差事图片（最多9张）
}
