package com.jobfirm.paymentservice.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.paymentservice.service.PaymentReleaseService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/release")
@RequiredArgsConstructor
public class PaymentReleaseController {

    private final PaymentReleaseService paymentReleaseService;

    /** 完全释放托管资金 */
    @PostMapping("/full/{orderPaymentId}")
    public Result<Void> releaseFull(@PathVariable Long orderPaymentId) {
        paymentReleaseService.releaseFull(orderPaymentId);
        return Result.success(null);
    }

    /** 比例释放托管资金 */
    @PostMapping("/partial/{orderPaymentId}")
    public Result<Void> releasePartial(@PathVariable Long orderPaymentId) {
        paymentReleaseService.releasePartial(orderPaymentId);
        return Result.success(null);
    }

}
