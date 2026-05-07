package com.jobfirm.paymentservice.mq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobfirm.infrastructure.config.RocketMQTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * PaymentService 支付请求消费者
 * 监听 order-pay 主题，处理支付请求，
 * 模拟支付处理后发送结果到 payment-result 主题
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = RocketMQTopics.ORDER_PAY_TOPIC,
        consumerGroup = RocketMQTopics.PAYMENT_CONSUMER_GROUP
)
public class PaymentResultConsumer implements RocketMQListener<String> {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(String message) {
        try {
            // 解析支付请求消息
            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.valueOf(payload.get("orderId").toString());
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            String payMethod = (String) payload.get("payMethod");

            log.info("收到支付请求: orderId={}, amount={}, payMethod={}", orderId, amount, payMethod);

            // 模拟支付处理（实际项目中此处调用第三方支付网关）
            boolean paymentSuccess = simulatePayment(orderId, amount, payMethod);

            // 构造支付结果并发送到 payment-result 主题
            String resultCode = paymentSuccess ? "SUCCESS" : "FAILED";
            String resultMessage = paymentSuccess
                    ? "orderId=" + orderId + " 支付成功，金额=" + amount
                    : "orderId=" + orderId + " 支付失败";

            Map<String, Object> resultPayload = Map.of(
                    "orderId", orderId,
                    "amount", amount,
                    "resultCode", resultCode,
                    "resultMessage", resultMessage
            );
            String resultJson = objectMapper.writeValueAsString(resultPayload);
            Message<String> resultMessageObj = MessageBuilder.withPayload(resultJson).build();

            rocketMQTemplate.send(RocketMQTopics.PAYMENT_RESULT_TOPIC, resultMessageObj);
            log.info("已发送支付结果消息 -> topic: {}, orderId: {}, result: {}",
                    RocketMQTopics.PAYMENT_RESULT_TOPIC, orderId, resultCode);

        } catch (Exception e) {
            log.error("处理支付请求消息失败: {}", message, e);
        }
    }

    /**
     * 模拟支付处理
     * 实际项目中应调用第三方支付网关或内部支付系统
     */
    private boolean simulatePayment(Long orderId, BigDecimal amount, String payMethod) {
        log.info("模拟支付处理中... orderId={}, amount={}, method={}", orderId, amount, payMethod);
        try {
            // 模拟网络延迟
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        // 模拟支付成功（可根据业务逻辑增加随机失败场景）
        return true;
    }
}
