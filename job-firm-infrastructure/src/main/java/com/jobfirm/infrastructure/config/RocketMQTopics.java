package com.jobfirm.infrastructure.config;

/**
 * RocketMQ 主题常量定义
 * 集中管理所有异步消息通信的主题名称
 */
public final class RocketMQTopics {

    private RocketMQTopics() {
        // 工具类，防止实例化
    }

    // ======================== 订单支付相关 ========================

    /**
     * 订单支付请求主题
     * OrderService → 发布支付请求
     * PaymentService → 消费并处理支付
     */
    public static final String ORDER_PAY_TOPIC = "order-pay";

    /**
     * 支付结果回调主题
     * PaymentService → 发布支付结果
     * OrderService → 消费并更新订单状态
     */
    public static final String PAYMENT_RESULT_TOPIC = "payment-result";

    /**
     * 订单状态变更主题
     * OrderService → 发布订单状态变更事件
     * 其他服务 → 订阅并进行相应的业务处理
     */
    public static final String ORDER_STATUS_TOPIC = "order-status";

    // ======================== 消费者组 ========================

    /** OrderService 消费者组：处理支付结果 */
    public static final String ORDER_CONSUMER_GROUP = "order-consumer-group";

    /** PaymentService 消费者组：处理支付请求 */
    public static final String PAYMENT_CONSUMER_GROUP = "payment-consumer-group";
}
