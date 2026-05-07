package com.jobfirm.api.job;

import com.jobfirm.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * job-service 的 Feign RPC 接口
 */
@FeignClient(name = "job-service")
public interface JobClient {

    /**
     * 根据ID查询差事详情
     */
    @GetMapping("/jobs/{id}")
    Result<Object> getJob(@PathVariable("id") Long id);

    /**
     * 查询所有差事列表
     */
    @GetMapping("/jobs")
    Result<Object> listJobs();

    /**
     * 递增差事订单数
     */
    @PostMapping("/jobs/{id}/increment-order")
    Result<Void> incrementOrderCount(@PathVariable("id") Long id);
}
