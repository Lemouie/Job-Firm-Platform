package com.jobfirm.job.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.job.module.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表 Mapper 接口
 * 提供基础的 CRUD 操作
 */
@Mapper
@DS("job_firm_order")
public interface OrderMapper extends BaseMapper<Order> {
}
