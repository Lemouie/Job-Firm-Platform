package com.jobfirm.job.service.impl;

import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.job.enums.OrderStatusEnum;
import com.jobfirm.job.module.entity.Order;
import com.jobfirm.job.mapper.OrderMapper;
import com.jobfirm.job.service.OrderProgressService;
import com.jobfirm.job.service.OrderService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单表 Service 实现类
 * 实现完整的订单状态机
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderProgressService orderProgressService;

    // ======================== 状态查询 ========================

    @Override
    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    @Override
    public List<Order> listByCustomer(Long customerId) {
        return orderMapper.selectList(
                new QueryWrapper<Order>()
                        .eq("customer_id", customerId)
                        .orderByDesc("id")
        );
    }

    @Override
    public List<Order> listByFirm(Long firmId) {
        return orderMapper.selectList(
                new QueryWrapper<Order>()
                        .eq("firm_id", firmId)
                        .orderByDesc("id")
        );
    }

    // ======================== 订单创建 ========================

    @Override
    @Transactional
    public void createOrder(Order order) {
        if (order.getJobId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "差事ID不能为空");
        }
        if (order.getCustomerId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "顾客ID不能为空");
        }
        if (order.getFirmId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "事务所ID不能为空");
        }
        if (order.getAmount() == null || order.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "订单金额必须大于0");
        }
        order.setStatus(OrderStatusEnum.PENDING);
        orderMapper.insert(order);
    }

    // ======================== 订单支付 ========================

    @Override
    @Transactional
    public String payOrder(Long orderId, String payMethod) {
        Order order = getById(orderId);
        validateStatus(order, OrderStatusEnum.PENDING);

        // 直接调用支付服务（TODO: 集成PaymentClient/Fegin调用）
        return "订单 " + orderId + " 已发起支付请求，支付方式：" + payMethod + "，请等待支付结果...";
    }

    @Override
    @Transactional
    public String handlePaymentResult(Long orderId, String resultCode) {
        Order order = getById(orderId);
        // 支付回调时，订单应在PENDING或PAID状态（PAID表示已发起但回调未处理完）
        if (order.getStatus() != OrderStatusEnum.PENDING && order.getStatus() != OrderStatusEnum.PAID) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
        }

        if ("SUCCESS".equalsIgnoreCase(resultCode)) {
            order.setStatus(OrderStatusEnum.PAID);
            orderMapper.updateById(order);
            return "订单 " + orderId + " 支付成功，状态已更新为 PAID";
        } else {
            order.setStatus(OrderStatusEnum.FAILED);
            orderMapper.updateById(order);
            return "订单 " + orderId + " 支付失败，状态已更新为 FAILED";
        }
    }

    // ======================== 订单执行 ========================

    @Override
    @Transactional
    public void executeOrder(Long id) {
        Order order = getById(id);
        validateStatus(order, OrderStatusEnum.PAID);
        order.setStatus(OrderStatusEnum.EXECUTING);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void completeExecution(Long id) {
        Order order = getById(id);
        validateStatus(order, OrderStatusEnum.EXECUTING);
        order.setStatus(OrderStatusEnum.EXECUTED);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void acceptOrder(Long id) {
        Order order = getById(id);
        validateStatus(order, OrderStatusEnum.EXECUTED);
        order.setStatus(OrderStatusEnum.ACCEPTED);
        orderMapper.updateById(order);
        // TODO: 调用支付系统释放收入到事务所
    }

    // ======================== 订单取消 ========================

    @Override
    @Transactional
    public void cancelByCustomer(Long id, String cancelReason) {
        Order order = getById(id);

        // PENDING, PAID -> CANCELLED + refund
        if (order.getStatus() == OrderStatusEnum.PENDING || order.getStatus() == OrderStatusEnum.PAID) {
            order.setStatus(OrderStatusEnum.CANCELLED);
            order.setCancelReason(cancelReason);
            orderMapper.updateById(order);
            // TODO: 调用支付系统退款
            return;
        }

        // EXECUTING+ -> cannot cancel
        throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
    }

    @Override
    @Transactional
    public void cancelByFirm(Long id, String cancelReason) {
        Order order = getById(id);

        // PENDING, PAID -> CANCELLED + refund
        if (order.getStatus() == OrderStatusEnum.PENDING || order.getStatus() == OrderStatusEnum.PAID) {
            order.setStatus(OrderStatusEnum.CANCELLED);
            order.setCancelReason(cancelReason);
            orderMapper.updateById(order);
            // TODO: 调用支付系统退款
            return;
        }

        // EXECUTING, EXECUTED -> CANCELLED + refund + escape_count++
        if (order.getStatus() == OrderStatusEnum.EXECUTING || order.getStatus() == OrderStatusEnum.EXECUTED) {
            order.setStatus(OrderStatusEnum.CANCELLED);
            order.setCancelReason(cancelReason);
            orderMapper.updateById(order);
            // TODO: 调用支付系统退款
            // TODO: 调用事务服务，事务所逃单次数+1
            return;
        }

        // Other states: invalid
        throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
    }

    // ======================== 售后与争议 ========================

    @Override
    @Transactional
    public void handleUnsatisfactory(Long id) {
        Order order = getById(id);
        // 顾客不满意：仅在 EXECUTED 状态下可触发
        if (order.getStatus() != OrderStatusEnum.EXECUTED) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
        }
        order.setStatus(OrderStatusEnum.ADJUDICATED);
        orderMapper.updateById(order);
        // TODO: 调用支付系统释放比例支付
    }

    @Override
    @Transactional
    public void disputeOrder(Long id) {
        Order order = getById(id);
        // 争议处理可以发生在 EXECUTED 或 ADJUDICATED 状态
        if (order.getStatus() != OrderStatusEnum.EXECUTED && order.getStatus() != OrderStatusEnum.ADJUDICATED) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
        }
        order.setStatus(OrderStatusEnum.ADJUDICATED);
        orderMapper.updateById(order);
        // TODO: 调用管理员系统处理争议
    }

    // ======================== 通用状态更新 ========================

    @Override
    @Transactional
    public void updateStatus(Long id, OrderStatusEnum status) {
        Order order = getById(id);
        OrderStatusEnum current = order.getStatus();

        // 校验状态转换合法性
        if (!isValidTransition(current, status)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID.getCode(),
                    "不能从 " + current + " 转换到 " + status);
        }

        order.setStatus(status);
        orderMapper.updateById(order);
    }

    // ======================== 订单进度 ========================

    @Override
    @Transactional
    public void updateProgress(Long orderId, String progress, String desc) {
        Order order = getById(orderId);
        // 仅 EXECUTING 和 EXECUTED 状态的订单可以更新进度
        if (order.getStatus() != OrderStatusEnum.EXECUTING && order.getStatus() != OrderStatusEnum.EXECUTED) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
        }
        orderProgressService.updateProgress(orderId, progress, desc);
    }

    // ======================== 内部工具方法 ========================

    /**
     * 校验订单状态是否为期望状态，否则抛出异常
     */
    private void validateStatus(Order order, OrderStatusEnum expected) {
        if (order.getStatus() != expected) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID.getCode(),
                    "订单状态不正确，期望: " + expected + "，当前: " + order.getStatus());
        }
    }

    /**
     * 校验状态转换是否合法
     */
    private boolean isValidTransition(OrderStatusEnum current, OrderStatusEnum target) {
        // 定义合法的状态转换映射
        switch (current) {
            case PENDING:
                return target == OrderStatusEnum.PAID
                        || target == OrderStatusEnum.FAILED
                        || target == OrderStatusEnum.CANCELLED;
            case PAID:
                return target == OrderStatusEnum.EXECUTING
                        || target == OrderStatusEnum.FAILED
                        || target == OrderStatusEnum.CANCELLED;
            case FAILED:
                return target == OrderStatusEnum.PENDING; // 允许重新支付
            case EXECUTING:
                return target == OrderStatusEnum.EXECUTED
                        || target == OrderStatusEnum.CANCELLED;
            case EXECUTED:
                return target == OrderStatusEnum.ACCEPTED
                        || target == OrderStatusEnum.ADJUDICATED
                        || target == OrderStatusEnum.CANCELLED;
            case ACCEPTED:
                return false; // 终态，不可再转换
            case CANCELLED:
                return false; // 终态
            case ADJUDICATED:
                return false; // 终态
            default:
                return false;
        }
    }
}
