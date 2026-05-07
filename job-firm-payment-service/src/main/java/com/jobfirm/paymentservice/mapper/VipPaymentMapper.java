package com.jobfirm.paymentservice.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.paymentservice.model.entity.VipPayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mapper：平台 VIP 支付记录
 * 对应表：payment_vip
 *
 * 平台收入 = VIP 支付成功金额
 */
@Mapper
@DS("job_firm_payment")
public interface VipPaymentMapper extends BaseMapper<VipPayment> {

    /**
     * 统计 VIP 支付成功金额（平台收入）
     * @param firmId 事务所ID（可为 null）
     * @param start 开始时间（可为 null）
     * @param end 结束时间（可为 null）
     * @return 收入总额（BigDecimal）
     */
    @Select({
            "<script>",
            "SELECT COALESCE(SUM(amount), 0) FROM payment_vip",
            "WHERE status = 'SUCCESS'",
            "<if test='firmId != null'> AND firm_id = #{firmId} </if>",
            "<if test='start != null'> AND created_at &gt;= #{start} </if>",
            "<if test='end != null'> AND created_at &lt; #{end} </if>",
            "</script>"
    })
    BigDecimal selectSumVipAmount(
            @Param("firmId") Long firmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
