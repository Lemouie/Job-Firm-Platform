package com.jobfirm.admin.controller;

import com.jobfirm.admin.model.entity.AdminActionLog;
import com.jobfirm.admin.service.AdminService;
import com.jobfirm.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /** 健康检查 */
    @GetMapping("/health")
    public Result<String> health() {
        return adminService.health();
    }

    /** 禁用用户 */
    @PostMapping("/users/{id}/disable")
    public Result<Void> disableUser(@PathVariable Long id) {
        return adminService.disableUser(id);
    }

    /** 审核通过事务所 */
    @PostMapping("/firms/{id}/approve")
    public Result<Void> approveFirm(@PathVariable Long id) {
        return adminService.approveFirm(id);
    }

    /** 驳回事务所 */
    @PostMapping("/firms/{id}/reject")
    public Result<Void> rejectFirm(@PathVariable Long id) {
        return adminService.rejectFirm(id);
    }

    /** 禁用事务所 */
    @PostMapping("/firms/{id}/disable")
    public Result<Void> disableFirm(@PathVariable Long id) {
        return adminService.disableFirm(id);
    }

    /** 查询所有差事 */
    @GetMapping("/jobs")
    public Result<List<Map<String, Object>>> listAllJobs() {
        return adminService.listAllJobs();
    }

    /** 查询所有订单 */
    @GetMapping("/orders")
    public Result<List<Map<String, Object>>> listAllOrders() {
        return adminService.listAllOrders();
    }

    /** 裁决订单纠纷 */
    @PostMapping("/orders/{id}/adjudicate")
    public Result<Void> adjudicateOrder(@PathVariable Long id) {
        return adminService.adjudicateOrder(id);
    }

    /** 查询归档记录 */
    @GetMapping("/archive-records")
    public Result<List<AdminActionLog>> listArchiveRecords() {
        return adminService.listArchiveRecords();
    }

    /** 平台统计信息 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getPlatformStats() {
        return adminService.getPlatformStats();
    }
}
