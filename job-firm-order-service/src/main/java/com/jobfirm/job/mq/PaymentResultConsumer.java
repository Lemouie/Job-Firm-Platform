package com.jobfirm.job.mq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobfirm.infrastructure.config.RocketMQTopics;
import com.jobfirm.job.enums.OrderStatusEnum;
import com.jobfirm.job.module.entity.Order;
import com.jobfirm.job.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * OrderService 支付结果消费者
 * 监听 payment-result 主题，根据支付结果更新订单状态
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = RocketMQTopics.PAYMENT_RESULT_TOPIC,
        consumerGroup = RocketMQTopics.ORDER_CONSUMER_GROUP
)
public class PaymentResultConsumer implements RocketMQListener<String> {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final OrderMQProducer orderMQProducer;

    @Override
    @Transactional
    public void onMessage(String message) {
        try {
            // 解析支付结果消息
            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.valueOf(payload.get("orderId").toString());
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            String resultCode = (String) payload.get("resultCode");

            log.info("收到支付结果消息: orderId={}, amount={}, result={}", orderId, amount, resultCode);

            // 查询订单当前状态
            Order order = orderService.getById(orderId);
            String oldStatus = order.getStatus().name();

            // 根据支付结果更新订单状态
            if ("SUCCESS".equalsIgnoreCase(resultCode)) {
                order.setStatus(OrderStatusEnum.PAID);
                log.info("订单 {} 支付成功，状态更新为 PAID", orderId);
            } else {
                order.setStatus(OrderStatusEnum.FAILED);
                log.info("订单 {} 支付失败，状态更新为 FAILED", orderId);
            }

            // 更新订单状态到数据库
            orderService.updateStatus(orderId, order.getStatus());

            // 发布订单状态变更消息
            orderMQProducer.sendOrderStatusChange(order, oldStatus);

        } catch (Exception e) {
            log.error("处理支付结果消息失败: {}", message, e);
        }
    }
}
