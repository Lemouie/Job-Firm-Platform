package com.jobfirm.job.service.impl;

import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.job.enums.OrderStatusEnum;
import com.jobfirm.job.mapper.OrderMapper;
import com.jobfirm.job.module.entity.Order;
import com.jobfirm.job.mq.OrderMQProducer;
import com.jobfirm.job.service.OrderProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OrderServiceImpl 单元测试
 * 测试完整的订单状态机：创建、支付、执行、验收、取消等流程
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderProgressService orderProgressService;

    @Mock
    private OrderMQProducer orderMQProducer;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderMapper, orderProgressService, orderMQProducer);
    }

    // ======================== 订单创建 ========================

    @Test
    @DisplayName("创建订单 - 成功，状态初始化为 PENDING")
    void testCreateOrder() {
        // Arrange
        Order order = new Order();
        order.setJobId(100L);
        order.setCustomerId(200L);
        order.setFirmId(300L);
        order.setAmount(new BigDecimal("99.99"));

        // Act
        orderService.createOrder(order);

        // Assert
        assertEquals(OrderStatusEnum.PENDING, order.getStatus());
        verify(orderMapper).insert(orderCaptor.capture());
        assertEquals(order, orderCaptor.getValue());
    }

    @Test
    @DisplayName("创建订单 - jobId 为空应抛出 PARAM_ERROR")
    void testCreateOrder_JobIdNull() {
        // Arrange
        Order order = new Order();
        order.setCustomerId(200L);
        order.setFirmId(300L);
        order.setAmount(new BigDecimal("99.99"));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(order));
        assertTrue(ex.getMessage().contains("差事ID不能为空"));
        verify(orderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建订单 - customerId 为空应抛出 PARAM_ERROR")
    void testCreateOrder_CustomerIdNull() {
        // Arrange
        Order order = new Order();
        order.setJobId(100L);
        order.setFirmId(300L);
        order.setAmount(new BigDecimal("99.99"));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(order));
        assertTrue(ex.getMessage().contains("顾客ID不能为空"));
        verify(orderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建订单 - firmId 为空应抛出 PARAM_ERROR")
    void testCreateOrder_FirmIdNull() {
        // Arrange
        Order order = new Order();
        order.setJobId(100L);
        order.setCustomerId(200L);
        order.setAmount(new BigDecimal("99.99"));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(order));
        assertTrue(ex.getMessage().contains("事务所ID不能为空"));
        verify(orderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建订单 - 金额为空应抛出 PARAM_ERROR")
    void testCreateOrder_AmountNull() {
        // Arrange
        Order order = new Order();
        order.setJobId(100L);
        order.setCustomerId(200L);
        order.setFirmId(300L);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(order));
        assertTrue(ex.getMessage().contains("订单金额必须大于0"));
        verify(orderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建订单 - 金额小于等于0应抛出 PARAM_ERROR")
    void testCreateOrder_AmountNonPositive() {
        // Arrange
        Order order = new Order();
        order.setJobId(100L);
        order.setCustomerId(200L);
        order.setFirmId(300L);
        order.setAmount(BigDecimal.ZERO);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(order));
        assertTrue(ex.getMessage().contains("订单金额必须大于0"));
        verify(orderMapper, never()).insert(any());
    }

    // ======================== 支付 ========================

    @Test
    @DisplayName("支付订单 - PENDING 状态发起支付，发送MQ消息")
    void testPayOrder() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setJobId(100L);
        order.setCustomerId(200L);
        order.setFirmId(300L);
        order.setAmount(new BigDecimal("99.99"));
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        String result = orderService.payOrder(orderId, "ALIPAY");

        // Assert
        assertTrue(result.contains("已发起支付请求"));
        verify(orderMapper).selectById(orderId);
        verify(orderMQProducer).sendPaymentRequest(order, "ALIPAY");
        // 状态保持 PENDING，等待回调
        assertEquals(OrderStatusEnum.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("支付订单 - 非 PENDING 状态应抛出 ORDER_STATUS_INVALID")
    void testPayOrder_WrongStatus() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.payOrder(orderId, "ALIPAY"));
        assertEquals(ErrorCode.ORDER_STATUS_INVALID.getCode(), ex.getMessage().contains("订单状态不正确") ? 6002 : 0);
        verify(orderMQProducer, never()).sendPaymentRequest(any(), any());
    }

    @Test
    @DisplayName("处理支付回调成功 - PENDING -> PAID")
    void testHandlePaymentResult_Success() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        String result = orderService.handlePaymentResult(orderId, "SUCCESS");

        // Assert
        assertEquals(OrderStatusEnum.PAID, order.getStatus());
        verify(orderMapper).updateById(order);
        assertTrue(result.contains("支付成功"));
    }

    @Test
    @DisplayName("处理支付回调失败 - PENDING -> FAILED")
    void testHandlePaymentResult_Failed() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        String result = orderService.handlePaymentResult(orderId, "FAILED");

        // Assert
        assertEquals(OrderStatusEnum.FAILED, order.getStatus());
        verify(orderMapper).updateById(order);
        assertTrue(result.contains("支付失败"));
    }

    @Test
    @DisplayName("处理支付回调 - 无效状态抛出 ORDER_STATUS_INVALID")
    void testHandlePaymentResult_InvalidStatus() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.handlePaymentResult(orderId, "SUCCESS"));
        assertEquals(ErrorCode.ORDER_STATUS_INVALID.getCode(), 6002);
        verify(orderMapper, never()).updateById(any());
    }

    // ======================== 订单状态机 ========================

    @Test
    @DisplayName("执行订单 - PAID -> EXECUTING")
    void testExecuteOrder() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PAID);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.executeOrder(orderId);

        // Assert
        assertEquals(OrderStatusEnum.EXECUTING, order.getStatus());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("执行订单 - 非 PAID 状态抛出 ORDER_STATUS_INVALID")
    void testExecuteOrder_WrongStatus() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class, () -> orderService.executeOrder(orderId));
        verify(orderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("完成执行 - EXECUTING -> EXECUTED")
    void testCompleteExecution() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.completeExecution(orderId);

        // Assert
        assertEquals(OrderStatusEnum.EXECUTED, order.getStatus());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("完成执行 - 非 EXECUTING 状态抛出 ORDER_STATUS_INVALID")
    void testCompleteExecution_WrongStatus() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PAID);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class, () -> orderService.completeExecution(orderId));
        verify(orderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("验收订单 - EXECUTED -> ACCEPTED")
    void testAcceptOrder() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTED);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.acceptOrder(orderId);

        // Assert
        assertEquals(OrderStatusEnum.ACCEPTED, order.getStatus());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("验收订单 - 非 EXECUTED 状态抛出 ORDER_STATUS_INVALID")
    void testAcceptOrder_WrongStatus() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class, () -> orderService.acceptOrder(orderId));
        verify(orderMapper, never()).updateById(any());
    }

    // ======================== 取消订单（顾客） ========================

    @Test
    @DisplayName("顾客取消订单 - PENDING -> CANCELLED")
    void testCancelByCustomer_Pending() {
        // Arrange
        Long orderId = 1L;
        String reason = "改变主意了";
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.cancelByCustomer(orderId, reason);

        // Assert
        assertEquals(OrderStatusEnum.CANCELLED, order.getStatus());
        assertEquals(reason, order.getCancelReason());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("顾客取消订单 - PAID -> CANCELLED")
    void testCancelByCustomer_Paid() {
        // Arrange
        Long orderId = 1L;
        String reason = "取消已支付订单";
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PAID);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.cancelByCustomer(orderId, reason);

        // Assert
        assertEquals(OrderStatusEnum.CANCELLED, order.getStatus());
        assertEquals(reason, order.getCancelReason());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("顾客取消订单 - EXECUTING 状态抛出 ORDER_CANNOT_CANCEL")
    void testCancelByCustomer_Executing() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.cancelByCustomer(orderId, "不能取消执行中订单"));
        assertEquals(ErrorCode.ORDER_CANNOT_CANCEL.getCode(),
                ex.getMessage() == null ? 0 : ex.getMessage().contains("不允许取消") ? 6003 : 0);
        verify(orderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("顾客取消订单 - EXECUTED 状态抛出 ORDER_CANNOT_CANCEL")
    void testCancelByCustomer_Executed() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTED);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderService.cancelByCustomer(orderId, "不能取消已执行订单"));
        verify(orderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("顾客取消订单 - ACCEPTED 状态抛出 ORDER_CANNOT_CANCEL")
    void testCancelByCustomer_Accepted() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.ACCEPTED);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderService.cancelByCustomer(orderId, "不能取消已验收订单"));
        verify(orderMapper, never()).updateById(any());
    }

    // ======================== 取消订单（事务所） ========================

    @Test
    @DisplayName("事务所取消订单 - PENDING -> CANCELLED")
    void testCancelByFirm_Pending() {
        // Arrange
        Long orderId = 1L;
        String reason = "事务繁忙";
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.cancelByFirm(orderId, reason);

        // Assert
        assertEquals(OrderStatusEnum.CANCELLED, order.getStatus());
        assertEquals(reason, order.getCancelReason());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("事务所取消订单 - PAID -> CANCELLED")
    void testCancelByFirm_Paid() {
        // Arrange
        Long orderId = 1L;
        String reason = "退款取消";
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PAID);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.cancelByFirm(orderId, reason);

        // Assert
        assertEquals(OrderStatusEnum.CANCELLED, order.getStatus());
        assertEquals(reason, order.getCancelReason());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("事务所取消订单 - EXECUTING -> CANCELLED（逃单计数+1）")
    void testCancelByFirm_Executing() {
        // Arrange
        Long orderId = 1L;
        String reason = "无法继续执行";
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.cancelByFirm(orderId, reason);

        // Assert
        assertEquals(OrderStatusEnum.CANCELLED, order.getStatus());
        assertEquals(reason, order.getCancelReason());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("事务所取消订单 - EXECUTED -> CANCELLED（逃单计数+1）")
    void testCancelByFirm_Executed() {
        // Arrange
        Long orderId = 1L;
        String reason = "已完成但取消";
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTED);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.cancelByFirm(orderId, reason);

        // Assert
        assertEquals(OrderStatusEnum.CANCELLED, order.getStatus());
        assertEquals(reason, order.getCancelReason());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("事务所取消订单 - ACCEPTED 状态抛出 ORDER_STATUS_INVALID")
    void testCancelByFirm_Accepted() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.ACCEPTED);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderService.cancelByFirm(orderId, "不能取消已验收订单"));
        verify(orderMapper, never()).updateById(any());
    }

    // ======================== getById ========================

    @Test
    @DisplayName("查询订单 - 存在返回订单")
    void testGetById_Found() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        Order result = orderService.getById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderMapper).selectById(orderId);
    }

    @Test
    @DisplayName("查询订单 - 不存在抛出 ORDER_NOT_FOUND")
    void testGetById_NotFound() {
        // Arrange
        Long orderId = 999L;
        when(orderMapper.selectById(orderId)).thenReturn(null);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.getById(orderId));
        assertEquals(ErrorCode.ORDER_NOT_FOUND, ex.getMessage().contains("订单不存在") ? ErrorCode.ORDER_NOT_FOUND : null);
        verify(orderMapper).selectById(orderId);
    }

    // ======================== updateStatus ========================

    @Test
    @DisplayName("更新状态 - PENDING -> PAID 合法转换")
    void testUpdateStatus_PendingToPaid() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.updateStatus(orderId, OrderStatusEnum.PAID);

        // Assert
        assertEquals(OrderStatusEnum.PAID, order.getStatus());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("更新状态 - PENDING -> ACCEPTED 非法转换抛出 ORDER_STATUS_INVALID")
    void testUpdateStatus_PendingToAccepted_Invalid() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderService.updateStatus(orderId, OrderStatusEnum.ACCEPTED));
        verify(orderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("更新状态 - ACCEPTED 终态不可转换")
    void testUpdateStatus_AcceptedIsFinal() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.ACCEPTED);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderService.updateStatus(orderId, OrderStatusEnum.CANCELLED));
        verify(orderMapper, never()).updateById(any());
    }

    // ======================== 售后与争议 ========================

    @Test
    @DisplayName("处理不满意 - EXECUTED -> ADJUDICATED")
    void testHandleUnsatisfactory() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTED);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.handleUnsatisfactory(orderId);

        // Assert
        assertEquals(OrderStatusEnum.ADJUDICATED, order.getStatus());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("处理不满意 - 非 EXECUTED 状态抛出 ORDER_STATUS_INVALID")
    void testHandleUnsatisfactory_WrongStatus() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class, () -> orderService.handleUnsatisfactory(orderId));
        verify(orderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("争议订单 - EXECUTED -> ADJUDICATED")
    void testDisputeOrder_FromExecuted() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTED);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.disputeOrder(orderId);

        // Assert
        assertEquals(OrderStatusEnum.ADJUDICATED, order.getStatus());
        verify(orderMapper).updateById(order);
    }

    @Test
    @DisplayName("争议订单 - 无效状态抛出 ORDER_STATUS_INVALID")
    void testDisputeOrder_WrongStatus() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PAID);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class, () -> orderService.disputeOrder(orderId));
        verify(orderMapper, never()).updateById(any());
    }

    // ======================== 订单进度 ========================

    @Test
    @DisplayName("更新进度 - EXECUTING 状态允许")
    void testUpdateProgress_Executing() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.EXECUTING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act
        orderService.updateProgress(orderId, "50%", "工作进行到一半");

        // Assert
        verify(orderProgressService).updateProgress(orderId, "50%", "工作进行到一半");
    }

    @Test
    @DisplayName("更新进度 - PENDING 状态抛出 ORDER_STATUS_INVALID")
    void testUpdateProgress_WrongStatus() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PENDING);

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderService.updateProgress(orderId, "50%", "test"));
        verify(orderProgressService, never()).updateProgress(any(), any(), any());
    }
}
