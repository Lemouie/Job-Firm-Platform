package com.jobfirm.paymentservice.service;

import com.jobfirm.paymentservice.model.dto.OrderPaymentCallbackDTO;
import com.jobfirm.paymentservice.model.dto.OrderPaymentCreateDTO;
import com.jobfirm.paymentservice.model.vo.OrderPaymentVO;

public interface OrderPaymentService {

    /** 查询差事支付记录 */
    OrderPaymentVO getOrderPayment(Long id);

    /** 创建差事支付记录 */
    Long createOrderPayment(OrderPaymentCreateDTO dto);

    /** 支付回调（成功/失败） */
    void handleOrderPaymentCallback(OrderPaymentCallbackDTO dto);

    /** 根据差事订单ID查询支付记录 */
    OrderPaymentVO getPaymentByOrderId(Long orderId);

    /** 退款 */
    void refundPayment(Long orderPaymentId);

}
