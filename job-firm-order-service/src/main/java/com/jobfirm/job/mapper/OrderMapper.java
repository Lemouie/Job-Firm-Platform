package com.jobfirm.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.job.module.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表 Mapper 接口
 * 提供基础的 CRUD 操作
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
