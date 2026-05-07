package com.jobfirm.job.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobfirm.infrastructure.config.RocketMQTopics;
import com.jobfirm.job.module.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OrderService RocketMQ 消息生产者
 * 负责发送支付请求和订单状态变更消息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMQProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 发送支付请求消息到 order-pay 主题
     * PaymentService 消费后执行支付处理
     *
     * @param order     订单实体
     * @param payMethod 支付方式
     */
    public void sendPaymentRequest(Order order, String payMethod) {
        try {
            Map<String, Object> payload = Map.of(
                    "orderId", order.getId(),
                    "customerId", order.getCustomerId(),
                    "firmId", order.getFirmId(),
                    "amount", order.getAmount(),
                    "payMethod", payMethod
            );
            String json = objectMapper.writeValueAsString(payload);
            Message<String> message = MessageBuilder.withPayload(json).build();

            rocketMQTemplate.send(RocketMQTopics.ORDER_PAY_TOPIC, message);
            log.info("已发送支付请求消息 -> topic: {}, orderId: {}, amount: {}",
                    RocketMQTopics.ORDER_PAY_TOPIC, order.getId(), order.getAmount());
        } catch (JsonProcessingException e) {
            log.error("支付请求消息序列化失败, orderId: {}", order.getId(), e);
            throw new RuntimeException("支付请求消息序列化失败", e);
        }
    }

    /**
     * 发送订单状态变更消息到 order-status 主题
     * 其他服务可订阅此主题以感知订单状态的变化
     *
     * @param order   订单实体
     * @param oldStatus 变更前的状态（可为 null）
     */
    public void sendOrderStatusChange(Order order, String oldStatus) {
        try {
            Map<String, Object> payload = Map.of(
                    "orderId", order.getId(),
                    "oldStatus", oldStatus,
                    "newStatus", order.getStatus().name(),
                    "customerId", order.getCustomerId(),
                    "firmId", order.getFirmId()
            );
            String json = objectMapper.writeValueAsString(payload);
            Message<String> message = MessageBuilder.withPayload(json).build();

            rocketMQTemplate.send(RocketMQTopics.ORDER_STATUS_TOPIC, message);
            log.info("已发送订单状态变更消息 -> topic: {}, orderId: {}, status: {} -> {}",
                    RocketMQTopics.ORDER_STATUS_TOPIC, order.getId(), oldStatus, order.getStatus());
        } catch (JsonProcessingException e) {
            log.error("订单状态变更消息序列化失败, orderId: {}", order.getId(), e);
            throw new RuntimeException("订单状态变更消息序列化失败", e);
        }
    }
}
