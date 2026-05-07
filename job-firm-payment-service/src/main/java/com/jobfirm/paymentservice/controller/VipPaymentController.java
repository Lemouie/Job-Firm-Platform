package com.jobfirm.paymentservice.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.paymentservice.model.dto.VipPaymentCallbackDTO;
import com.jobfirm.paymentservice.model.dto.VipPaymentCreateDTO;
import com.jobfirm.paymentservice.model.vo.VipPaymentVO;
import com.jobfirm.paymentservice.service.VipPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/vip")
@RequiredArgsConstructor
public class VipPaymentController {

    /** 查询 VIP 支付记录 */
    @GetMapping("/view/{id}")
    public Result<VipPaymentVO> getVipPayment(@PathVariable Long id) {
        VipPaymentVO vo = vipPaymentService.getVipPayment(id);
        return Result.success(vo);
    }

    private final VipPaymentService vipPaymentService;

    /** 创建 VIP 支付记录（事务所充值 VIP） */
    @PostMapping("/create")
    public Result<Long> createVipPayment(@RequestBody VipPaymentCreateDTO dto) {
        Long id = vipPaymentService.createVipPayment(dto);
        return Result.success(id);
    }

    /** VIP 支付回调（成功/失败） */
    @PostMapping("/callback")
    public Result<Void> vipPaymentCallback(@RequestBody VipPaymentCallbackDTO dto) {
        vipPaymentService.handleVipPaymentCallback(dto);
        return Result.success(null);
    }

}
