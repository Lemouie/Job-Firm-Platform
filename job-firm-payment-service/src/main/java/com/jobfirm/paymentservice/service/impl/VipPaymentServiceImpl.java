package com.jobfirm.paymentservice.service.impl;


import com.jobfirm.paymentservice.mapper.VipPaymentMapper;
import com.jobfirm.paymentservice.model.dto.VipPaymentCallbackDTO;
import com.jobfirm.paymentservice.model.dto.VipPaymentCreateDTO;
import com.jobfirm.paymentservice.model.entity.VipPayment;
import com.jobfirm.paymentservice.model.vo.VipPaymentVO;
import com.jobfirm.paymentservice.service.VipPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 平台 VIP 支付接收服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VipPaymentServiceImpl implements VipPaymentService {
    private final VipPaymentMapper vipPaymentMapper;

    @Override
    public Long createVipPayment(VipPaymentCreateDTO dto) {
        VipPayment payment = new VipPayment();
        payment.setFirmId(dto.getFirmId());
        payment.setAmount(dto.getAmount());
        payment.setPayMethod(dto.getPayMethod());
        payment.setStatus("PENDING");

        vipPaymentMapper.insert(payment);
        return payment.getId();
    }

    @Override
    public void handleVipPaymentCallback(VipPaymentCallbackDTO dto) {
        VipPayment payment = vipPaymentMapper.selectById(dto.getPaymentId());
        if (payment == null) return;

        if (Boolean.TRUE.equals(dto.getSuccess())) {
            payment.setStatus("SUCCESS");

            // TODO: 款项进入平台收益账户-银行卡
            // bankClient.transferToPlatformRevenue(payment.getAmount());

            // TODO: 调用事务所服务，更新事务所 VIP 状态
            // firmClient.updateVipStatus(payment.getFirmId(), true);
            log.info("VIP payment success: firmId={}, amount={}, transactionId={}",
                    payment.getFirmId(), payment.getAmount(), dto.getTransactionId());
        } else {
            payment.setStatus("FAILED");
            log.warn("VIP payment failed: paymentId={}, transactionId={}",
                    dto.getPaymentId(), dto.getTransactionId());
        }

        payment.setTransactionId(dto.getTransactionId());
        vipPaymentMapper.updateById(payment);
    }

    @Override
    public VipPaymentVO getVipPayment(Long id) {
        VipPayment payment = vipPaymentMapper.selectById(id);
        if (payment == null) return null;

        VipPaymentVO vo = new VipPaymentVO();
        BeanUtils.copyProperties(payment, vo);
        return vo;
    }
}
