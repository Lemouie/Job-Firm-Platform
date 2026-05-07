package com.jobfirm.paymentservice.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.paymentservice.model.dto.OrderPaymentCallbackDTO;
import com.jobfirm.paymentservice.model.dto.OrderPaymentCreateDTO;
import com.jobfirm.paymentservice.model.vo.OrderPaymentVO;
import com.jobfirm.paymentservice.service.OrderPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/order")
@RequiredArgsConstructor
public class OrderPaymentController {

    private final OrderPaymentService orderPaymentService;

    /** 查询订单支付记录 */
    @GetMapping("/view/{id}")
    public Result<OrderPaymentVO> getOrderPayment(@PathVariable Long id) {
        OrderPaymentVO vo = orderPaymentService.getOrderPayment(id);
        return Result.success(vo);
    }

    /** 创建订单支付记录 */
    @PostMapping("/create")
    public Result<Long> createOrderPayment(@RequestBody OrderPaymentCreateDTO dto) {
        Long id = orderPaymentService.createOrderPayment(dto);
        return Result.success(id);
    }

    /** 订单支付回调（成功/失败） */
    @PostMapping("/callback")
    public Result<Void> orderPaymentCallback(@RequestBody OrderPaymentCallbackDTO dto) {
        orderPaymentService.handleOrderPaymentCallback(dto);
        return Result.success(null);
    }

}
