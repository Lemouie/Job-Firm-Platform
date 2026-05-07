package com.jobfirm.firmservice.service.impl;

import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.firmservice.model.dto.FirmDTO;
import com.jobfirm.firmservice.model.entity.Firm;
import com.jobfirm.firmservice.mapper.FirmMapper;
import com.jobfirm.firmservice.service.FirmService;
import com.jobfirm.firmservice.model.vo.FirmVO;
import com.jobfirm.firmservice.model.vo.StatsVO;
import com.jobfirm.firmservice.model.vo.VipVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FirmServiceImpl
 * 事务所服务实现，严格按照功能表实现
 */
@Service
@RequiredArgsConstructor
public class FirmServiceImpl implements FirmService {

    private final FirmMapper firmMapper;

    /**
     * 创建事务所
     * 每个 CEO 只能创建一个事务所
     */
    @Override
    @Transactional
    public FirmVO createFirm(FirmDTO dto, Long ceoId) {
        // 校验是否已有事务所
        List<Firm> existing = firmMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Firm>()
                        .eq("ceo_id", ceoId)
        );
        if (!existing.isEmpty()) {
            throw new BusinessException(ErrorCode.FIRM_ALREADY_EXISTS);
        }

        Firm firm = new Firm();
        firm.setCeoId(ceoId);
        firm.setName(dto.getName());
        firm.setDescription(dto.getDescription());
        firm.setLogoUrl(dto.getLogoUrl());
        firm.setStatus("PENDING"); // 默认待审核
        firm.setVipStatus("NONE");

        firmMapper.insert(firm);

        return toVO(firm);
    }

    /**
     * 获取事务所审核状态
     */
    @Override
    public String getFirmStatus(Long id) {
        Firm firm = firmMapper.selectById(id);
        return firm != null ? firm.getStatus() : "NOT_FOUND";
    }

    /**
     * 列出所有事务所（VIP 权重更高）
     */
    @Override
    public List<FirmVO> listFirms() {
        List<Firm> firms = firmMapper.selectList(null);
        // 简单排序：VIP ACTIVE > 其他
        return firms.stream()
                .sorted(
                        Comparator
                                .comparing((Firm f) -> !"ACTIVE".equals(f.getVipStatus()))
                                .thenComparing(Firm::getCreatedAt, Comparator.reverseOrder())
                )
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取事务所 VIP 状态
     */
    @Override
    public VipVO getVipStatus(Long id) {
        Firm firm = getFirmById(id);
        VipVO vo = new VipVO();
        vo.setVipStatus(firm.getVipStatus());
        vo.setVipExpireTime(firm.getVipExpireTime());
        return vo;
    }

    // ---------------- 审核与状态管理 ----------------

    @Override
    @Transactional
    public void approveFirm(Long id) {
        Firm firm = getFirmById(id);
        if (!"PENDING".equals(firm.getStatus())) {
            throw new BusinessException(ErrorCode.FIRM_NOT_APPROVED);
        }
        firm.setStatus("APPROVED");
        firmMapper.updateById(firm);
    }

    @Override
    @Transactional
    public void rejectFirm(Long id) {
        Firm firm = getFirmById(id);
        if (!"PENDING".equals(firm.getStatus())) {
            throw new BusinessException(ErrorCode.FIRM_NOT_APPROVED);
        }
        firm.setStatus("REJECTED");
        firmMapper.updateById(firm);
    }

    @Override
    @Transactional
    public void disableFirm(Long id) {
        Firm firm = getFirmById(id);
        firm.setStatus("DISABLED");
        firmMapper.updateById(firm);
    }

    // ---------------- 收入管理 ----------------

    @Override
    @Transactional
    public void addRevenue(Long id, BigDecimal amount) {
        Firm firm = getFirmById(id);
        if (!"APPROVED".equals(firm.getStatus())) {
            throw new BusinessException(ErrorCode.FIRM_NOT_APPROVED);
        }
        BigDecimal current = firm.getRevenue() != null ? firm.getRevenue() : BigDecimal.ZERO;
        firm.setRevenue(current.add(amount));
        firmMapper.updateById(firm);
    }

    @Override
    @Transactional
    public void withdrawRevenue(Long id, BigDecimal amount) {
        Firm firm = getFirmById(id);
        if (!"APPROVED".equals(firm.getStatus())) {
            throw new BusinessException(ErrorCode.FIRM_NOT_APPROVED);
        }
        BigDecimal current = firm.getRevenue() != null ? firm.getRevenue() : BigDecimal.ZERO;
        if (current.compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.FIRM_INSUFFICIENT_BALANCE);
        }
        firm.setRevenue(current.subtract(amount));
        firmMapper.updateById(firm);
    }

    @Override
    public StatsVO getFirmStats(Long id) {
        Firm firm = getFirmById(id);
        BigDecimal revenue = firm.getRevenue() != null ? firm.getRevenue() : BigDecimal.ZERO;
        StatsVO vo = new StatsVO();
        vo.setTotalAmount(revenue.doubleValue());
        // 暂时只返回总收入，日/月/年统计后续由 PaymentClient 提供
        return vo;
    }

    // ---------------- 工具方法 ----------------

    private Firm getFirmById(Long id) {
        Firm firm = firmMapper.selectById(id);
        if (firm == null) {
            throw new BusinessException(ErrorCode.FIRM_NOT_FOUND);
        }
        return firm;
    }

    private FirmVO toVO(Firm firm) {
        FirmVO vo = new FirmVO();
        vo.setId(firm.getId());
        vo.setCeoId(firm.getCeoId());
        vo.setName(firm.getName());
        vo.setDescription(firm.getDescription());
        vo.setLogoUrl(firm.getLogoUrl());
        vo.setStatus(firm.getStatus());
        vo.setVipStatus(firm.getVipStatus());
        vo.setVipExpireTime(firm.getVipExpireTime());
        vo.setRevenue(firm.getRevenue());

        return vo;
    }
}
