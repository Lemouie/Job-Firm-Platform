package com.jobfirm.admin.service.impl;

import com.jobfirm.admin.mapper.AdminActionLogMapper;
import com.jobfirm.admin.model.entity.AdminActionLog;
import com.jobfirm.admin.service.AdminService;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminActionLogMapper adminActionLogMapper;

    @Override
    public Result<Void> disableUser(Long userId) {
        log.info("Admin disable user: {}", userId);
        // TODO: Feign call to user-service to disable user
        log.warn("Feign call to user-service not yet implemented. User {} disable stub.", userId);
        return Result.fail(ErrorCode.SERVICE_UNAVAILABLE.getCode(), "用户服务暂未集成，无法禁用用户");
    }

    @Override
    public Result<Void> approveFirm(Long firmId) {
        log.info("Admin approve firm: {}", firmId);
        // TODO: Feign call to firm-service to approve firm
        log.warn("Feign call to firm-service not yet implemented. Firm {} approve stub.", firmId);
        return Result.fail(ErrorCode.SERVICE_UNAVAILABLE.getCode(), "事务所服务暂未集成，无法审核通过事务所");
    }

    @Override
    public Result<Void> rejectFirm(Long firmId) {
        log.info("Admin reject firm: {}", firmId);
        // TODO: Feign call to firm-service to reject firm
        log.warn("Feign call to firm-service not yet implemented. Firm {} reject stub.", firmId);
        return Result.fail(ErrorCode.SERVICE_UNAVAILABLE.getCode(), "事务所服务暂未集成，无法驳回事务所");
    }

    @Override
    public Result<Void> disableFirm(Long firmId) {
        log.info("Admin disable firm: {}", firmId);
        // TODO: Feign call to firm-service to disable firm
        log.warn("Feign call to firm-service not yet implemented. Firm {} disable stub.", firmId);
        return Result.fail(ErrorCode.SERVICE_UNAVAILABLE.getCode(), "事务所服务暂未集成，无法禁用事务所");
    }

    @Override
    public Result<List<Map<String, Object>>> listAllJobs() {
        log.info("Admin list all jobs");
        // TODO: Feign call to job-service
        log.warn("Feign call to job-service not yet implemented. Returning empty list.");
        return Result.success(Collections.emptyList());
    }

    @Override
    public Result<List<Map<String, Object>>> listAllOrders() {
        log.info("Admin list all orders");
        // TODO: Feign call to order-service
        log.warn("Feign call to order-service not yet implemented. Returning empty list.");
        return Result.success(Collections.emptyList());
    }

    @Override
    public Result<Void> adjudicateOrder(Long orderId) {
        log.info("Admin adjudicate order: {}", orderId);
        // TODO: Feign call to order-service
        log.warn("Feign call to order-service not yet implemented. Order {} adjudicate stub.", orderId);
        return Result.fail(ErrorCode.SERVICE_UNAVAILABLE.getCode(), "订单服务暂未集成，无法裁决订单纠纷");
    }

    @Override
    public Result<List<AdminActionLog>> listArchiveRecords() {
        log.info("Admin list archive records");
        // TODO: Feign call to archive-service
        log.warn("Feign call to archive-service not yet implemented. Returning empty list.");
        return Result.success(Collections.emptyList());
    }

    @Override
    public Result<Map<String, Object>> getPlatformStats() {
        log.info("Admin get platform stats");
        // TODO: Aggregate stats from all services via Feign
        log.warn("Platform stats stub - returning basic stats.");
        Map<String, Object> stats = Map.of(
                "totalUsers", 0,
                "totalFirms", 0,
                "totalJobs", 0,
                "totalOrders", 0,
                "totalRevenue", 0
        );
        return Result.success(stats);
    }

    @Override
    public Result<String> health() {
        return Result.success("Admin Service is running");
    }
}
