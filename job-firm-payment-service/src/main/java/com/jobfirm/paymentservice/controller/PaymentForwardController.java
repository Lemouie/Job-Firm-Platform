package com.jobfirm.paymentservice.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.paymentservice.service.PaymentForwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/forward")
@RequiredArgsConstructor
public class PaymentForwardController {

    private final PaymentForwardService paymentForwardService;

    /** 将托管金额转发到事务所收益（记账） */
    @PostMapping("/{orderPaymentId}")
    public Result<Void> forward(@PathVariable Long orderPaymentId) {
        paymentForwardService.forwardToFirmWallet(orderPaymentId);
        return Result.success(null);
    }
}
