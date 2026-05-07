package com.jobfirm.paymentservice.service.impl;

import com.jobfirm.paymentservice.mapper.OrderPaymentMapper;
import com.jobfirm.paymentservice.mapper.VipPaymentMapper;
import com.jobfirm.paymentservice.service.PaymentStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 支付统计服务
 *
 * 事务所收入 = 差事收入（released_amount）
 * 平台收入 = VIP 支付收入（amount）
 */
@Service
@RequiredArgsConstructor
public class PaymentStatisticsServiceImpl implements PaymentStatisticsService {

    private final OrderPaymentMapper orderPaymentMapper;
    private final VipPaymentMapper vipPaymentMapper;

    // ============================
    // 事务所收入统计（差事收入）
    // ============================

    @Override
    public BigDecimal getFirmYesterdayRevenue(Long firmId) {
        LocalDate d = LocalDate.now().minusDays(1);
        return orderPaymentMapper.selectSumReleasedAmount(
                firmId,
                d.atStartOfDay(),
                d.plusDays(1).atStartOfDay()
        );
    }

    @Override
    public BigDecimal getFirmMonthlyRevenue(Long firmId) {
        LocalDate d = LocalDate.now().withDayOfMonth(1);
        return orderPaymentMapper.selectSumReleasedAmount(
                firmId,
                d.atStartOfDay(),
                d.plusMonths(1).atStartOfDay()
        );
    }

    @Override
    public BigDecimal getFirmYearlyRevenue(Long firmId) {
        LocalDate d = LocalDate.now().withDayOfYear(1);
        return orderPaymentMapper.selectSumReleasedAmount(
                firmId,
                d.atStartOfDay(),
                d.plusYears(1).atStartOfDay()
        );
    }

    @Override
    public BigDecimal getFirmTotalRevenue(Long firmId) {
        return orderPaymentMapper.selectSumReleasedAmount(firmId, null, null);
    }

    // ============================
    // 平台收入统计（VIP 支付）
    // ============================

    @Override
    public BigDecimal getPlatformYesterdayRevenue() {
        LocalDate d = LocalDate.now().minusDays(1);
        return vipPaymentMapper.selectSumVipAmount(
                null,
                d.atStartOfDay(),
                d.plusDays(1).atStartOfDay()
        );
    }

    @Override
    public BigDecimal getPlatformMonthlyRevenue() {
        LocalDate d = LocalDate.now().withDayOfMonth(1);
        return vipPaymentMapper.selectSumVipAmount(
                null,
                d.atStartOfDay(),
                d.plusMonths(1).atStartOfDay()
        );
    }

    @Override
    public BigDecimal getPlatformYearlyRevenue() {
        LocalDate d = LocalDate.now().withDayOfYear(1);
        return vipPaymentMapper.selectSumVipAmount(
                null,
                d.atStartOfDay(),
                d.plusYears(1).atStartOfDay()
        );
    }

    @Override
    public BigDecimal getPlatformTotalRevenue() {
        return vipPaymentMapper.selectSumVipAmount(null, null, null);
    }
}
