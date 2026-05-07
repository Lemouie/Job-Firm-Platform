package com.jobfirm.api.payment;

import com.jobfirm.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * payment-service 的 Feign RPC 接口
 */
@FeignClient(name = "payment-service")
public interface PaymentClient {

    /**
     * 创建订单支付记录
     */
    @PostMapping("/payment/order/create")
    Result<Long> createOrderPayment(@RequestBody Object dto);

    /**
     * 订单支付回调
     */
    @PostMapping("/payment/order/callback")
    Result<Void> orderPaymentCallback(@RequestBody Object dto);

    /**
     * 完全释放托管资金
     */
    @PostMapping("/payment/release/full/{orderPaymentId}")
    Result<Void> releaseFull(@PathVariable("orderPaymentId") Long orderPaymentId);

    /**
     * 比例释放托管资金
     */
    @PostMapping("/payment/release/partial/{orderPaymentId}")
    Result<Void> releasePartial(@PathVariable("orderPaymentId") Long orderPaymentId);

    /**
     * 查询订单支付记录
     */
    @GetMapping("/payment/order/view/{id}")
    Result<Object> getOrderPayment(@PathVariable("id") Long id);
}
