package com.jobfirm.job.service;

import com.jobfirm.job.enums.OrderStatusEnum;
import com.jobfirm.job.module.entity.Order;
import java.util.List;

/**
 * 订单表 Service 接口
 * 定义订单相关的业务方法
 */
public interface OrderService {

    /** ------------------------------订单展示-------------------------------------*/
    /** 查询订单详情 */
    Order getById(Long id);

    /** 查询顾客订单 */
    List<Order> listByCustomer(Long customerId);

    /** 查询事务所订单 */
    List<Order> listByFirm(Long firmId);

    /** ------------------------------订单创建-------------------------------------*/
    /** 创建订单 */
    void createOrder(Order order);

    /** ------------------------------订单支付-------------------------------------*/
    /** 发起订单支付（支付费用） */
    String payOrder(Long orderId, String payMethod);

    /** 接收支付结果并更新订单状态 */
    String handlePaymentResult(Long orderId, String resultCode);

    /** ------------------------------订单状态变更-------------------------------------*/
    /** 更新订单状态 */
    void updateStatus(Long id, OrderStatusEnum status);

    /** ------------------------------订单执行-------------------------------------*/
    /** 事务所执行任务（PAID -> EXECUTING） */
    void executeOrder(Long id);

    /** 事务所完成任务（EXECUTING -> EXECUTED） */
    void completeExecution(Long id);

    /** 顾客验收（EXECUTED -> ACCEPTED），触发支付释放 */
    void acceptOrder(Long id);

    /** ------------------------------订单售后处理-------------------------------------*/
    /** 顾客取消订单 */
    void cancelByCustomer(Long id, String cancelReason);

    /** 事务所取消订单 */
    void cancelByFirm(Long id, String cancelReason);

    /** 处理顾客不满意（标记争议） */
    void handleUnsatisfactory(Long id);

    /** ------------------------------订单争议处理（管理员）-------------------------------------*/
    /** 申请争议处理 */
    void disputeOrder(Long id);

    /** ------------------------------订单进度-------------------------------------*/
    /** 更新订单进度 */
    void updateProgress(Long orderId, String progress, String desc);
}
