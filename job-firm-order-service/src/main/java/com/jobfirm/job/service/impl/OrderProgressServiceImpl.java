package com.jobfirm.job.service.impl;

import com.jobfirm.job.enums.OrderProgressEnum;
import com.jobfirm.job.module.entity.OrderProgress;
import com.jobfirm.job.mapper.OrderProgressMapper;
import com.jobfirm.job.service.OrderProgressService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 订单进度表 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class OrderProgressServiceImpl implements OrderProgressService {

    private final OrderProgressMapper orderProgressMapper;

    @Override
    public List<OrderProgress> listByOrderId(Long orderId) {
        return orderProgressMapper.selectList(
                new QueryWrapper<OrderProgress>()
                        .eq("order_id", orderId)
                        .orderByDesc("id")
        );
    }

    @Override
    public OrderProgress getProgressByOrderId(Long orderId) {
        return orderProgressMapper.selectOne(
                new QueryWrapper<OrderProgress>()
                        .eq("order_id", orderId)
                        .orderByDesc("id")
                        .last("LIMIT 1")
        );
    }

    @Override
    public void addProgress(OrderProgress progress) {
        orderProgressMapper.insert(progress);
    }

    @Override
    public void updateProgress(Long id, String progress, String desc) {
        OrderProgress orderProgress = orderProgressMapper.selectById(id);
        if (orderProgress != null) {
            orderProgress.setProgress(OrderProgressEnum.valueOf(progress));
            orderProgress.setProgressDesc(desc);
            orderProgressMapper.updateById(orderProgress);
        }
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        orderProgressMapper.delete(
                new QueryWrapper<OrderProgress>()
                        .eq("order_id", orderId)
        );
    }
}
