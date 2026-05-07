package com.jobfirm.job.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.job.module.entity.OrderProgress;
import com.jobfirm.job.service.OrderProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单进度表 Controller
 * 提供订单进度相关的 RESTful API
 */
@RestController
@RequestMapping("/order-progress")
@RequiredArgsConstructor
public class OrderProgressController {

    private final OrderProgressService orderProgressService;

    /**
     * 查询订单进度列表
     */
    @GetMapping("/view/list/{orderId}")
    public Result<List<OrderProgress>> listProgress(@PathVariable Long orderId,
                                                     @RequestHeader("X-User-Id") Long userId,
                                                     @RequestHeader("X-User-Role") String userRole) {
        return Result.success(orderProgressService.listByOrderId(orderId));
    }

    /**
     * 获取订单最新进度
     */
    @GetMapping("/view/latest/{orderId}")
    public Result<OrderProgress> getLatestProgress(@PathVariable Long orderId,
                                                    @RequestHeader("X-User-Id") Long userId,
                                                    @RequestHeader("X-User-Role") String userRole) {
        OrderProgress progress = orderProgressService.getProgressByOrderId(orderId);
        return Result.success(progress);
    }

    /**
     * 添加订单进度
     */
    @PostMapping("/add")
    public Result<Void> addProgress(@RequestBody OrderProgress progress,
                                    @RequestHeader("X-User-Id") Long userId,
                                    @RequestHeader("X-User-Role") String userRole) {
        orderProgressService.addProgress(progress);
        return Result.success(null);
    }

    /**
     * 更新订单进度
     */
    @PutMapping("/update/{id}")
    public Result<Void> updateProgress(@PathVariable Long id,
                                       @RequestParam String progress,
                                       @RequestParam(required = false) String desc,
                                       @RequestHeader("X-User-Id") Long userId,
                                       @RequestHeader("X-User-Role") String userRole) {
        orderProgressService.updateProgress(id, progress, desc);
        return Result.success(null);
    }

    /**
     * 删除订单进度
     */
    @DeleteMapping("/delete/{orderId}")
    public Result<Void> deleteProgress(@PathVariable Long orderId,
                                       @RequestHeader("X-User-Id") Long userId,
                                       @RequestHeader("X-User-Role") String userRole) {
        orderProgressService.deleteByOrderId(orderId);
        return Result.success(null);
    }
}
