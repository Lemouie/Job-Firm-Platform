package com.jobfirm.job.service;

import com.jobfirm.job.module.entity.OrderProgress;
import java.util.List;

/**
 * 订单进度表 Service 接口
 * 定义订单进度相关的业务方法
 */
public interface OrderProgressService {
    List<OrderProgress> listByOrderId(Long orderId);           // 查询订单进度
    OrderProgress getProgressByOrderId(Long orderId);          // 获取订单最新进度
    void addProgress(OrderProgress progress);                  // 添加订单进度
    void updateProgress(Long id, String progress, String desc); // 更新订单进度
    void deleteByOrderId(Long orderId);                        // 删除订单进度
}
