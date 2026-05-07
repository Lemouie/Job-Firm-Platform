package com.jobfirm.firmservice.model.vo;

import lombok.Data;

/**
 * 流水统计展示对象
 */
@Data
public class StatsVO {
    private Double dailyAmount;   // 日流水
    private Double monthlyAmount; // 月流水
    private Double yearlyAmount;  // 年度流水
    private Double totalAmount;   // 总流水
}
