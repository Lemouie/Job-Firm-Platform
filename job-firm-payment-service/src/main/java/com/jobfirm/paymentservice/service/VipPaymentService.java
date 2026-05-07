package com.jobfirm.paymentservice.service;

import com.jobfirm.paymentservice.model.dto.VipPaymentCallbackDTO;
import com.jobfirm.paymentservice.model.dto.VipPaymentCreateDTO;
import com.jobfirm.paymentservice.model.vo.VipPaymentVO;

public interface VipPaymentService {

    /** 查询 VIP 支付记录 */
    VipPaymentVO getVipPayment(Long id);

    /** 创建 VIP 支付记录 */
    Long createVipPayment(VipPaymentCreateDTO dto);

    /** 接受 VIP 支付（成功/失败） */
    void handleVipPaymentCallback(VipPaymentCallbackDTO dto);

}
