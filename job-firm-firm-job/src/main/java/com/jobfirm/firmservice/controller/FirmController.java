package com.jobfirm.firmservice.controller;

import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.common.result.Result;
import com.jobfirm.firmservice.model.dto.FirmDTO;
import com.jobfirm.firmservice.model.dto.TaskDTO;
import com.jobfirm.firmservice.model.dto.ProgressDTO;
import com.jobfirm.firmservice.service.FirmService;
import com.jobfirm.firmservice.model.vo.FirmVO;
import com.jobfirm.firmservice.model.vo.VipVO;
import com.jobfirm.firmservice.model.vo.OrderVO;
import com.jobfirm.firmservice.model.vo.StatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * FirmController
 * 事务所服务接口，严格按照功能表实现
 */
@RestController
@RequestMapping("/firms")
@RequiredArgsConstructor
public class FirmController {

    private final FirmService firmService;

    // ---------------- 事务所创建与审核 ----------------
    @PostMapping("/create")
    public Result<FirmVO> createFirm(@RequestBody FirmDTO dto,
                                     @RequestHeader("X-User-Id") Long ceoId) {
        return Result.success(firmService.createFirm(dto, ceoId));
    }

    @GetMapping("/{id}/status")
    public Result<String> getFirmStatus(@PathVariable Long id) {
        return Result.success(firmService.getFirmStatus(id));
    }

    // ---------------- 事务所展示 ----------------
    @GetMapping
    public Result<List<FirmVO>> listFirms() {
        return Result.success(firmService.listFirms());
    }

    // ---------------- 事务所 VIP ----------------
    @PostMapping("/{id}/vip/purchase")
    public Result<Void> purchaseVip(@PathVariable Long id) {
        // TODO 调用 PaymentClient 完成支付 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/{id}/vip/status")
    public Result<VipVO> getVipStatus(@PathVariable Long id) {
        return Result.success(firmService.getVipStatus(id));
    }

    // ---------------- 事务所金融运营 ----------------
    @PostMapping("/{id}/withdraw")
    public Result<Void> withdraw(@PathVariable Long id,
                                 @RequestParam Double amount) {
        firmService.withdrawRevenue(id, BigDecimal.valueOf(amount));
        return Result.success(null);
    }

    // ---------------- 事务所差事运营 ----------------
    @PostMapping("/{id}/tasks")
    public Result<Void> applyTask(@PathVariable Long id,
                                  @RequestBody TaskDTO dto) {
        // TODO 调用 TaskClient 创建差事 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @PutMapping("/{id}/tasks/{taskId}")
    public Result<Void> updateTask(@PathVariable Long id,
                                   @PathVariable Long taskId,
                                   @RequestBody TaskDTO dto) {
        // TODO 调用 TaskClient 修改差事 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @PostMapping("/{id}/tasks/{taskId}/publish")
    public Result<Void> publishTask(@PathVariable Long id,
                                    @PathVariable Long taskId) {
        // TODO 调用 TaskClient 上架差事 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @PostMapping("/{id}/tasks/{taskId}/unpublish")
    public Result<Void> unpublishTask(@PathVariable Long id,
                                      @PathVariable Long taskId) {
        // TODO 调用 TaskClient 下架差事 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // ---------------- 事务所订单运营 ----------------
    @GetMapping("/{id}/orders")
    public Result<List<OrderVO>> listFirmOrders(@PathVariable Long id) {
        // TODO 调用 OrderClient 获取订单列表 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @PostMapping("/{id}/orders/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id,
                                    @PathVariable Long orderId) {
        // TODO 调用 OrderClient 取消订单 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @PostMapping("/{id}/orders/{orderId}/accept")
    public Result<Void> acceptOrder(@PathVariable Long id,
                                    @PathVariable Long orderId) {
        // TODO 调用 OrderClient 确认接单 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @PostMapping("/{id}/orders/{orderId}/progress")
    public Result<Void> updateProgress(@PathVariable Long id,
                                       @PathVariable Long orderId,
                                       @RequestBody ProgressDTO progress) {
        // TODO 调用 OrderClient 更新进度 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @PostMapping("/{id}/orders/{orderId}/complete")
    public Result<Void> completeOrder(@PathVariable Long id,
                                      @PathVariable Long orderId) {
        // TODO 调用 OrderClient 完成订单 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // ---------------- 事务所统计 ----------------
    @GetMapping("/{id}/stats/daily")
    public Result<StatsVO> getDailyStats(@PathVariable Long id) {
        // TODO 调用 PaymentClient 日流水统计 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/{id}/stats/monthly")
    public Result<StatsVO> getMonthlyStats(@PathVariable Long id) {
        // TODO 调用 PaymentClient 月流水统计 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/{id}/stats/yearly")
    public Result<StatsVO> getYearlyStats(@PathVariable Long id) {
        // TODO 调用 PaymentClient 年度流水统计 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/{id}/stats/total")
    public Result<StatsVO> getTotalStats(@PathVariable Long id) {
        return Result.success(firmService.getFirmStats(id));
    }

    // ---------------- 管理员功能 ----------------
    @GetMapping("/admin")
    public Result<List<FirmVO>> listAllFirmsForAdmin() {
        return Result.success(firmService.listFirms());
    }

    @PostMapping("/admin/{id}/approve")
    public Result<Void> approveFirm(@PathVariable Long id) {
        firmService.approveFirm(id);
        return Result.success(null);
    }

    @PostMapping("/admin/{id}/reject")
    public Result<Void> rejectFirm(@PathVariable Long id) {
        firmService.rejectFirm(id);
        return Result.success(null);
    }

    @PostMapping("/admin/{id}/disable")
    public Result<Void> disableFirm(@PathVariable Long id) {
        firmService.disableFirm(id);
        return Result.success(null);
    }

    @PostMapping("/admin/{id}/complaints/{complaintId}/resolve")
    public Result<Void> resolveComplaint(@PathVariable Long id,
                                         @PathVariable Long complaintId) {
        // TODO 调用 AdminClient 处理投诉 — 暂未实现
        throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
    }
}
