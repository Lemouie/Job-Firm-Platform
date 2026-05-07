package com.jobfirm.job.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.job.enums.OrderStatusEnum;
import com.jobfirm.job.module.entity.Order;
import com.jobfirm.job.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 订单表 Controller
 * 提供订单相关的 RESTful API
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 查询订单详情
     */
    @GetMapping("/view/{id}")
    public Result<Order> getOrder(@PathVariable Long id,
                                  @RequestHeader("X-User-Id") Long userId,
                                  @RequestHeader("X-User-Role") String userRole) {
        return Result.success(orderService.getById(id));
    }

    /**
     * 查询顾客订单列表
     */
    @GetMapping("/view/customer/{customerId}")
    public Result<List<Order>> listCustomerOrders(@PathVariable Long customerId,
                                                   @RequestHeader("X-User-Id") Long userId,
                                                   @RequestHeader("X-User-Role") String userRole) {
        return Result.success(orderService.listByCustomer(customerId));
    }

    /**
     * 查询事务所订单列表
     */
    @GetMapping("/view/firm/{firmId}")
    public Result<List<Order>> listFirmOrders(@PathVariable Long firmId,
                                               @RequestHeader("X-User-Id") Long userId,
                                               @RequestHeader("X-User-Role") String userRole) {
        return Result.success(orderService.listByFirm(firmId));
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Void> createOrder(@RequestBody Order order,
                                    @RequestHeader("X-User-Id") Long userId,
                                    @RequestHeader("X-User-Role") String userRole) {
        orderService.createOrder(order);
        return Result.success(null);
    }

    /**
     * 发起订单支付（支付费用）
     */
    @PostMapping("/{orderId}/pay")
    public Result<String> payOrder(@PathVariable Long orderId,
                                   @RequestParam String payMethod,
                                   @RequestHeader("X-User-Id") Long userId,
                                   @RequestHeader("X-User-Role") String userRole) {
        String result = orderService.payOrder(orderId, payMethod);
        return Result.success(result);
    }

    /**
     * 接收支付结果并更新订单状态
     */
    @PostMapping("/{orderId}/result")
    public Result<String> receivePaymentResult(@PathVariable Long orderId,
                                               @RequestParam String resultCode,
                                               @RequestHeader("X-User-Id") Long userId,
                                               @RequestHeader("X-User-Role") String userRole) {
        String result = orderService.handlePaymentResult(orderId, resultCode);
        return Result.success(result);
    }

    /**
     * 事务所执行任务（PAID -> EXECUTING）
     */
    @PostMapping("/{id}/execute")
    public Result<Void> executeOrder(@PathVariable Long id,
                                     @RequestHeader("X-User-Id") Long userId,
                                     @RequestHeader("X-User-Role") String userRole) {
        orderService.executeOrder(id);
        return Result.success(null);
    }

    /**
     * 事务所完成任务（EXECUTING -> EXECUTED）
     */
    @PostMapping("/{id}/complete-execution")
    public Result<Void> completeExecution(@PathVariable Long id,
                                          @RequestHeader("X-User-Id") Long userId,
                                          @RequestHeader("X-User-Role") String userRole) {
        orderService.completeExecution(id);
        return Result.success(null);
    }

    /**
     * 顾客验收（EXECUTED -> ACCEPTED）
     */
    @PostMapping("/{id}/accept")
    public Result<Void> acceptOrder(@PathVariable Long id,
                                    @RequestHeader("X-User-Id") Long userId,
                                    @RequestHeader("X-User-Role") String userRole) {
        orderService.acceptOrder(id);
        return Result.success(null);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestParam OrderStatusEnum status,
                                     @RequestHeader("X-User-Id") Long userId,
                                     @RequestHeader("X-User-Role") String userRole) {
        orderService.updateStatus(id, status);
        return Result.success(null);
    }

    /**
     * 顾客取消订单
     */
    @PostMapping("/{id}/cancel/customer")
    public Result<Void> cancelByCustomer(@PathVariable Long id,
                                         @RequestParam(required = false) String cancelReason,
                                         @RequestHeader("X-User-Id") Long userId,
                                         @RequestHeader("X-User-Role") String userRole) {
        orderService.cancelByCustomer(id, cancelReason);
        return Result.success(null);
    }

    /**
     * 事务所取消订单
     */
    @PostMapping("/{id}/cancel/firm")
    public Result<Void> cancelByFirm(@PathVariable Long id,
                                     @RequestParam(required = false) String cancelReason,
                                     @RequestHeader("X-User-Id") Long userId,
                                     @RequestHeader("X-User-Role") String userRole) {
        orderService.cancelByFirm(id, cancelReason);
        return Result.success(null);
    }

    /**
     * 处理顾客不满意
     */
    @PostMapping("/{id}/handle/unsatisfactory")
    public Result<Void> handleUnsatisfactory(@PathVariable Long id,
                                             @RequestHeader("X-User-Id") Long userId,
                                             @RequestHeader("X-User-Role") String userRole) {
        orderService.handleUnsatisfactory(id);
        return Result.success(null);
    }

    /**
     * 申请订单争议处理
     */
    @PostMapping("/{id}/dispute")
    public Result<Void> disputeOrder(@PathVariable Long id,
                                     @RequestHeader("X-User-Id") Long userId,
                                     @RequestHeader("X-User-Role") String userRole) {
        orderService.disputeOrder(id);
        return Result.success(null);
    }
}
