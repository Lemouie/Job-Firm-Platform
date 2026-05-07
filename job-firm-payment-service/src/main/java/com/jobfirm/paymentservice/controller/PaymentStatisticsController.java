package com.jobfirm.paymentservice.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.paymentservice.service.PaymentStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payment/statistics")
@RequiredArgsConstructor
public class PaymentStatisticsController {

    private final PaymentStatisticsService statisticsService;

    // ============================
    // 事务所流水（差事收入）
    // ============================

    /** 统计事务所昨日流水 */
    @GetMapping("/firm/{firmId}/yesterday")
    public Result<BigDecimal> firmYesterday(@PathVariable Long firmId) {
        return Result.success(statisticsService.getFirmYesterdayRevenue(firmId));
    }

    /** 统计事务所本月流水 */
    @GetMapping("/firm/{firmId}/month")
    public Result<BigDecimal> firmMonth(@PathVariable Long firmId) {
        return Result.success(statisticsService.getFirmMonthlyRevenue(firmId));
    }

    /** 统计事务所本年度流水 */
    @GetMapping("/firm/{firmId}/year")
    public Result<BigDecimal> firmYear(@PathVariable Long firmId) {
        return Result.success(statisticsService.getFirmYearlyRevenue(firmId));
    }

    /** 统计事务所累计总流水 */
    @GetMapping("/firm/{firmId}/total")
    public Result<BigDecimal> firmTotal(@PathVariable Long firmId) {
        return Result.success(statisticsService.getFirmTotalRevenue(firmId));
    }

    // ============================
    // 平台流水（VIP 收入）
    // ============================

    /** 统计平台昨日流水 */
    @GetMapping("/platform/yesterday")
    public Result<BigDecimal> platformYesterday() {
        return Result.success(statisticsService.getPlatformYesterdayRevenue());
    }

    /** 统计平台本月流水 */
    @GetMapping("/platform/month")
    public Result<BigDecimal> platformMonth() {
        return Result.success(statisticsService.getPlatformMonthlyRevenue());
    }

    /** 统计平台本年度流水 */
    @GetMapping("/platform/year")
    public Result<BigDecimal> platformYear() {
        return Result.success(statisticsService.getPlatformYearlyRevenue());
    }

    /** 统计平台累计总流水 */
    @GetMapping("/platform/total")
    public Result<BigDecimal> platformTotal() {
        return Result.success(statisticsService.getPlatformTotalRevenue());
    }
}
