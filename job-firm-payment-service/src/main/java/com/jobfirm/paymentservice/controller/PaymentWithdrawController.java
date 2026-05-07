package com.jobfirm.paymentservice.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.paymentservice.service.PaymentWithdrawService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payment/withdraw")
@RequiredArgsConstructor
public class PaymentWithdrawController {

    private final PaymentWithdrawService paymentWithdrawService;

    /** 提现事务所收益 */
    @PostMapping("/by/firm")
    public Result<Void> withdraw(@RequestBody WithdrawDTO dto) {
        paymentWithdrawService.withdrawFirmRevenue(dto.getFirmId(), dto.getAmount());
        return Result.success(null);
    }

    @Data
    public static class WithdrawDTO {
        private Long firmId;
        private BigDecimal amount;
    }
}
