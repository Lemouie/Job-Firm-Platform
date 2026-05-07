package com.jobfirm.paymentservice.service.impl;

import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.paymentservice.service.PaymentWithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 支付提现模块实现类
 * 功能：提现事务所收益
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWithdrawServiceImpl implements PaymentWithdrawService {

    @Override
    public void withdrawFirmRevenue(Long firmId, BigDecimal amount) {

        // 1. 校验参数
        if (firmId == null) {
            throw new BusinessException(ErrorCode.FIRM_NOT_FOUND);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        // 2. TODO：查询事务所收益字段（调用 firm-service）
        // BigDecimal currentRevenue = firmClient.getRevenue(firmId);
        // if (currentRevenue == null) {
        //     throw new BusinessException(ErrorCode.FIRM_NOT_FOUND);
        // }

        // 3. TODO：校验收益是否足够
        // if (currentRevenue.compareTo(amount) < 0) {
        //     throw new BusinessException(ErrorCode.FIRM_INSUFFICIENT_BALANCE);
        // }

        // 4. TODO：扣减事务所收益字段
        // firmClient.reduceRevenue(firmId, amount);

        // 5. TODO：调用第三方支付平台执行提现（银行卡）
        // bankClient.withdrawToFirmBankCard(firmId, amount);

        // 6. 提现完成（模拟）
        log.info("Withdraw success: firmId={}, amount={}", firmId, amount);
    }
}
