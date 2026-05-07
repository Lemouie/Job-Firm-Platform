package com.jobfirm.paymentservice.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.paymentservice.model.entity.OrderPayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mapper：差事支付记录（托管）
 * 对应表：payment_order
 *
 * 事务所收入 = 差事支付释放金额（released_amount）
 */
@Mapper
@DS("job_firm_payment")
public interface OrderPaymentMapper extends BaseMapper<OrderPayment> {

    /**
     * 统计差事支付释放给事务所的金额（事务所收入）
     * @param firmId 事务所ID（可为 null）
     * @param start 开始时间（可为 null）
     * @param end 结束时间（可为 null）
     * @return 收入总额（BigDecimal）
     */
    @Select({
            "<script>",
            "SELECT COALESCE(SUM(released_amount), 0) FROM payment_order",
            "WHERE released_amount &gt; 0",
            "<if test='firmId != null'> AND firm_id = #{firmId} </if>",
            "<if test='start != null'> AND updated_at &gt;= #{start} </if>",
            "<if test='end != null'> AND updated_at &lt; #{end} </if>",
            "</script>"
    })
    BigDecimal selectSumReleasedAmount(
            @Param("firmId") Long firmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
