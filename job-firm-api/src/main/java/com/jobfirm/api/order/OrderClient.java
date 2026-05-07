package com.jobfirm.api.order;

import com.jobfirm.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * order-service 的 Feign RPC 接口
 */
@FeignClient(name = "order-service")
public interface OrderClient {

    /**
     * 创建订单
     */
    @PostMapping("/orders/create")
    Result<Void> createOrder(@RequestBody Object order);

    /**
     * 查询订单详情
     */
    @GetMapping("/orders/view/{id}")
    Result<Object> getOrder(@PathVariable("id") Long id);

    /**
     * 更新订单状态
     */
    @PutMapping("/orders/{id}/status")
    Result<Void> updateStatus(@PathVariable("id") Long id,
                              @RequestParam("status") String status);

    /**
     * 顾客取消订单
     */
    @PostMapping("/orders/{id}/cancel/customer")
    Result<Void> cancelByCustomer(@PathVariable("id") Long id,
                                  @RequestParam("reason") String reason);

    /**
     * 事务所取消订单
     */
    @PostMapping("/orders/{id}/cancel/firm")
    Result<Void> cancelByFirm(@PathVariable("id") Long id,
                              @RequestParam("reason") String reason);
}
