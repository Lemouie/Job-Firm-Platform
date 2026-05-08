package com.jobfirm.admin.service;

import com.jobfirm.admin.model.entity.AdminActionLog;
import com.jobfirm.common.result.Result;

import java.util.List;
import java.util.Map;

public interface AdminService {

    /** 禁用用户 */
    Result<Void> disableUser(Long userId);

    /** 审核通过事务所 */
    Result<Void> approveFirm(Long firmId);

    /** 驳回事务所 */
    Result<Void> rejectFirm(Long firmId);

    /** 禁用事务所 */
    Result<Void> disableFirm(Long firmId);

    /** 查询所有差事 */
    Result<List<Map<String, Object>>> listAllJobs();

    /** 查询所有订单 */
    Result<List<Map<String, Object>>> listAllOrders();

    /** 裁决订单纠纷 */
    Result<Void> adjudicateOrder(Long orderId);

    /** 查询归档记录 */
    Result<List<AdminActionLog>> listArchiveRecords();

    /** 平台统计信息 */
    Result<Map<String, Object>> getPlatformStats();

    /** 健康检查 */
    Result<String> health();
}
