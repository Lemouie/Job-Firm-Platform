package com.jobfirm.paymentservice.service;

import java.math.BigDecimal;

/**
 * 支付统计服务
 * 功能：
 * - 统计事务所流水
 * - 统计平台流水
 * 范围：昨日、月度、年度、累计总流水
 */
public interface PaymentStatisticsService {

    /** 统计事务所昨日流水 */
    BigDecimal getFirmYesterdayRevenue(Long firmId);

    /** 统计事务所本月流水 */
    BigDecimal  getFirmMonthlyRevenue(Long firmId);

    /** 统计事务所本年度流水 */
    BigDecimal  getFirmYearlyRevenue(Long firmId);

    /** 统计事务所累计总流水 */
    BigDecimal  getFirmTotalRevenue(Long firmId);

    /** 统计平台昨日流水 */
    BigDecimal  getPlatformYesterdayRevenue();

    /** 统计平台本月流水 */
    BigDecimal  getPlatformMonthlyRevenue();

    /** 统计平台本年度流水 */
    BigDecimal  getPlatformYearlyRevenue();

    /** 统计平台累计总流水 */
    BigDecimal  getPlatformTotalRevenue();
}
