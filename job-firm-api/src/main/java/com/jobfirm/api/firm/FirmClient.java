package com.jobfirm.api.firm;

import com.jobfirm.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * firm-service 的 Feign RPC 接口
 */
@FeignClient(name = "firm-service")
public interface FirmClient {

    /**
     * 增加事务所收入
     */
    @PostMapping("/firms/{id}/revenue")
    Result<Void> addRevenue(@PathVariable("id") Long id,
                            @RequestParam("amount") Double amount);

    /**
     * 事务所提现
     */
    @PostMapping("/firms/{id}/withdraw")
    Result<Void> withdrawRevenue(@PathVariable("id") Long id,
                                 @RequestParam("amount") Double amount);

    /**
     * 查询事务所状态
     */
    @GetMapping("/firms/{id}/status")
    Result<String> getFirmStatus(@PathVariable("id") Long id);

    /**
     * 递增事务所跑单次数
     */
    @PostMapping("/firms/{id}/escape-count")
    Result<Void> incrementEscapeCount(@PathVariable("id") Long id);
}
