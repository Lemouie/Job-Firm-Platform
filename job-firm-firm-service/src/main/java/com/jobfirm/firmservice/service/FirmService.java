package com.jobfirm.firmservice.service;

import com.jobfirm.firmservice.model.dto.FirmDTO;
import com.jobfirm.firmservice.model.vo.FirmVO;
import com.jobfirm.firmservice.model.vo.StatsVO;
import com.jobfirm.firmservice.model.vo.VipVO;

import java.math.BigDecimal;
import java.util.List;

public interface FirmService {
    FirmVO createFirm(FirmDTO dto, Long ceoId);
    String getFirmStatus(Long id);
    List<FirmVO> listFirms();
    VipVO getVipStatus(Long id);

    // 审核与状态管理
    void approveFirm(Long id);
    void rejectFirm(Long id);
    void disableFirm(Long id);

    // 收入管理
    void addRevenue(Long id, BigDecimal amount);
    void withdrawRevenue(Long id, BigDecimal amount);
    StatsVO getFirmStats(Long id);
}
