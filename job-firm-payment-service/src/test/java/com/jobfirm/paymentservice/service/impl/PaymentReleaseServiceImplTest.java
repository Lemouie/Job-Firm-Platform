package com.jobfirm.paymentservice.service.impl;

import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.paymentservice.enums.OrderPaymentStatusEnum;
import com.jobfirm.paymentservice.mapper.OrderPaymentMapper;
import com.jobfirm.paymentservice.model.dto.OrderPaymentCallbackDTO;
import com.jobfirm.paymentservice.model.dto.OrderPaymentCreateDTO;
import com.jobfirm.paymentservice.model.entity.OrderPayment;
import com.jobfirm.paymentservice.service.PaymentForwardService;
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
 * 支付服务（托管工作流）单元测试
 * 涵盖：
 * - PaymentReleaseServiceImpl：完全释放、比例释放
 * - OrderPaymentServiceImpl：创建支付记录、支付回调处理、退款
 */
@ExtendWith(MockitoExtension.class)
class PaymentReleaseServiceImplTest {

    @Mock
    private OrderPaymentMapper orderPaymentMapper;

    @Mock
    private PaymentForwardService paymentForwardService;

    @Captor
    private ArgumentCaptor<OrderPayment> paymentCaptor;

    // 被测试的服务实例
    private PaymentReleaseServiceImpl paymentReleaseService;
    private OrderPaymentServiceImpl orderPaymentService;

    @BeforeEach
    void setUp() {
        paymentReleaseService = new PaymentReleaseServiceImpl(orderPaymentMapper, paymentForwardService);
        orderPaymentService = new OrderPaymentServiceImpl(orderPaymentMapper);
    }

    // ======================== PaymentReleaseServiceImpl: releaseFull ========================

    @Test
    @DisplayName("完全释放 - LOCKED -> RELEASED，调用 forwardToFirmWallet")
    void testReleaseFull() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.LOCKED);
        payment.setLockedAmount(new BigDecimal("100.00"));

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act
        paymentReleaseService.releaseFull(paymentId);

        // Assert
        assertEquals(OrderPaymentStatusEnum.RELEASED, payment.getStatus());
        assertEquals(new BigDecimal("100.00"), payment.getReleasedAmount());
        verify(orderPaymentMapper).updateById(payment);
        verify(paymentForwardService).forwardToFirmWallet(paymentId);
    }

    @Test
    @DisplayName("完全释放 - PENDING 状态抛出 PAYMENT_STATUS_INVALID")
    void testReleaseFull_WrongStatus() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.PENDING);

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> paymentReleaseService.releaseFull(paymentId));
        assertEquals(ErrorCode.PAYMENT_STATUS_INVALID.getCode(), 7002);
        verify(orderPaymentMapper, never()).updateById(any());
        verify(paymentForwardService, never()).forwardToFirmWallet(any());
    }

    @Test
    @DisplayName("完全释放 - PAYMENT_NOT_FOUND")
    void testReleaseFull_NotFound() {
        // Arrange
        Long paymentId = 999L;
        when(orderPaymentMapper.selectById(paymentId)).thenReturn(null);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> paymentReleaseService.releaseFull(paymentId));
        assertEquals(ErrorCode.PAYMENT_NOT_FOUND.getCode(), 7001);
        verify(orderPaymentMapper, never()).updateById(any());
        verify(paymentForwardService, never()).forwardToFirmWallet(any());
    }

    @Test
    @DisplayName("完全释放 - REFUNDED 状态抛出 PAYMENT_STATUS_INVALID")
    void testReleaseFull_RefundedStatus() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.REFUNDED);

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> paymentReleaseService.releaseFull(paymentId));
        assertEquals(7002, 7002);
        verify(orderPaymentMapper, never()).updateById(any());
        verify(paymentForwardService, never()).forwardToFirmWallet(any());
    }

    // ======================== PaymentReleaseServiceImpl: releasePartial ========================

    @Test
    @DisplayName("比例释放 - LOCKED -> PARTIAL_RELEASED，forward 调用")
    void testReleasePartial() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.LOCKED);
        payment.setLockedAmount(new BigDecimal("100.00"));
        payment.setRefundedAmount(new BigDecimal("20.00"));
        payment.setReleasedAmount(new BigDecimal("80.00"));

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act
        paymentReleaseService.releasePartial(paymentId);

        // Assert
        assertEquals(OrderPaymentStatusEnum.PARTIAL_RELEASED, payment.getStatus());
        assertEquals(new BigDecimal("80.00"), payment.getReleasedAmount());
        assertEquals(new BigDecimal("20.00"), payment.getRefundedAmount());
        verify(orderPaymentMapper).updateById(payment);
        verify(paymentForwardService).forwardToFirmWallet(paymentId);
    }

    @Test
    @DisplayName("比例释放 - 未设置 releasedAmount 时自动计算")
    void testReleasePartial_AutoCalculateReleasedAmount() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.LOCKED);
        payment.setLockedAmount(new BigDecimal("100.00"));
        payment.setRefundedAmount(new BigDecimal("30.00"));
        // releasedAmount 为 null → 自动计算 = locked - refunded = 70

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act
        paymentReleaseService.releasePartial(paymentId);

        // Assert
        assertEquals(OrderPaymentStatusEnum.PARTIAL_RELEASED, payment.getStatus());
        assertEquals(new BigDecimal("70.00"), payment.getReleasedAmount());
        verify(orderPaymentMapper).updateById(payment);
        verify(paymentForwardService).forwardToFirmWallet(paymentId);
    }

    @Test
    @DisplayName("比例释放 - 锁定金额不够抛出 PAYMENT_STATUS_INVALID")
    void testReleasePartial_AmountExceedsLocked() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.LOCKED);
        payment.setLockedAmount(new BigDecimal("100.00"));
        payment.setRefundedAmount(new BigDecimal("60.00"));
        payment.setReleasedAmount(new BigDecimal("60.00")); // 60+60=120 > 100

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> paymentReleaseService.releasePartial(paymentId));
        verify(orderPaymentMapper, never()).updateById(any());
        verify(paymentForwardService, never()).forwardToFirmWallet(any());
    }

    @Test
    @DisplayName("比例释放 - PENDING 状态抛出 PAYMENT_STATUS_INVALID")
    void testReleasePartial_WrongStatus() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.PENDING);

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> paymentReleaseService.releasePartial(paymentId));
        verify(orderPaymentMapper, never()).updateById(any());
        verify(paymentForwardService, never()).forwardToFirmWallet(any());
    }

    @Test
    @DisplayName("比例释放 - 支付记录不存在抛出 PAYMENT_NOT_FOUND")
    void testReleasePartial_NotFound() {
        // Arrange
        Long paymentId = 999L;
        when(orderPaymentMapper.selectById(paymentId)).thenReturn(null);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> paymentReleaseService.releasePartial(paymentId));
        verify(orderPaymentMapper, never()).updateById(any());
        verify(paymentForwardService, never()).forwardToFirmWallet(any());
    }

    // ======================== OrderPaymentServiceImpl: createOrderPayment ========================

    @Test
    @DisplayName("创建支付记录 - 状态初始化为 PENDING")
    void testCreateOrderPayment() {
        // Arrange
        OrderPaymentCreateDTO dto = new OrderPaymentCreateDTO();
        dto.setOrderId(100L);
        dto.setCustomerId(200L);
        dto.setFirmId(300L);
        dto.setAmount(new BigDecimal("99.99"));
        dto.setPayMethod("ALIPAY");

        when(orderPaymentMapper.insert(any(OrderPayment.class))).thenAnswer(invocation -> {
            OrderPayment payment = invocation.getArgument(0);
            payment.setId(1L); // 模拟 MyBatis-Plus 自动生成 ID
            return 1;
        });

        // Act
        Long resultId = orderPaymentService.createOrderPayment(dto);

        // Assert
        assertNotNull(resultId);
        assertEquals(1L, resultId);
        verify(orderPaymentMapper).insert(paymentCaptor.capture());
        OrderPayment captured = paymentCaptor.getValue();
        assertEquals(OrderPaymentStatusEnum.PENDING, captured.getStatus());
        assertEquals(dto.getOrderId(), captured.getOrderId());
        assertEquals(dto.getCustomerId(), captured.getCustomerId());
        assertEquals(dto.getFirmId(), captured.getFirmId());
        assertEquals(dto.getAmount(), captured.getAmount());
        assertEquals(dto.getPayMethod(), captured.getPayMethod());
    }

    // ======================== OrderPaymentServiceImpl: handleOrderPaymentCallback ========================

    @Test
    @DisplayName("支付回调成功 - PENDING -> LOCKED，设置锁定金额")
    void testHandlePaymentCallback_Success() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.PENDING);
        payment.setAmount(new BigDecimal("99.99"));

        OrderPaymentCallbackDTO dto = new OrderPaymentCallbackDTO();
        dto.setPaymentId(paymentId);
        dto.setTransactionId("TXN123456");
        dto.setSuccess(true);

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act
        orderPaymentService.handleOrderPaymentCallback(dto);

        // Assert
        assertEquals(OrderPaymentStatusEnum.LOCKED, payment.getStatus());
        assertEquals(new BigDecimal("99.99"), payment.getLockedAmount());
        assertEquals("TXN123456", payment.getTransactionId());
        verify(orderPaymentMapper).updateById(payment);
    }

    @Test
    @DisplayName("支付回调失败 - PENDING -> FAILED")
    void testHandlePaymentCallback_Failure() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.PENDING);

        OrderPaymentCallbackDTO dto = new OrderPaymentCallbackDTO();
        dto.setPaymentId(paymentId);
        dto.setTransactionId("TXN_ERROR");
        dto.setSuccess(false);

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act
        orderPaymentService.handleOrderPaymentCallback(dto);

        // Assert
        assertEquals(OrderPaymentStatusEnum.FAILED, payment.getStatus());
        assertNull(payment.getLockedAmount()); // 失败时不设置锁定金额
        assertEquals("TXN_ERROR", payment.getTransactionId());
        verify(orderPaymentMapper).updateById(payment);
    }

    @Test
    @DisplayName("支付回调 - 支付记录不存在时静默返回")
    void testHandlePaymentCallback_PaymentNotFound() {
        // Arrange
        OrderPaymentCallbackDTO dto = new OrderPaymentCallbackDTO();
        dto.setPaymentId(999L);
        dto.setTransactionId("TXN_NF");
        dto.setSuccess(true);

        when(orderPaymentMapper.selectById(999L)).thenReturn(null);

        // Act
        orderPaymentService.handleOrderPaymentCallback(dto);

        // Assert
        verify(orderPaymentMapper, never()).updateById(any());
    }

    // ======================== OrderPaymentServiceImpl: refundPayment ========================

    @Test
    @DisplayName("退款 - LOCKED -> REFUNDED")
    void testRefundPayment() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.LOCKED);
        payment.setLockedAmount(new BigDecimal("100.00"));
        payment.setAmount(new BigDecimal("100.00"));

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act
        orderPaymentService.refundPayment(paymentId);

        // Assert
        assertEquals(OrderPaymentStatusEnum.REFUNDED, payment.getStatus());
        assertEquals(new BigDecimal("100.00"), payment.getRefundedAmount());
        verify(orderPaymentMapper).updateById(payment);
    }

    @Test
    @DisplayName("退款 - lockedAmount 为 null 时使用 amount 作为退款金额")
    void testRefundPayment_LockedAmountNull() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.LOCKED);
        payment.setLockedAmount(null);
        payment.setAmount(new BigDecimal("50.00"));

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act
        orderPaymentService.refundPayment(paymentId);

        // Assert
        assertEquals(OrderPaymentStatusEnum.REFUNDED, payment.getStatus());
        assertEquals(new BigDecimal("50.00"), payment.getRefundedAmount());
        verify(orderPaymentMapper).updateById(payment);
    }

    @Test
    @DisplayName("退款 - PENDING 状态抛出 PAYMENT_STATUS_INVALID")
    void testRefundPayment_WrongStatus() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.PENDING);

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderPaymentService.refundPayment(paymentId));
        assertEquals(ErrorCode.PAYMENT_STATUS_INVALID.getCode(), 7002);
        verify(orderPaymentMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("退款 - 支付记录不存在抛出 PAYMENT_NOT_FOUND")
    void testRefundPayment_NotFound() {
        // Arrange
        Long paymentId = 999L;
        when(orderPaymentMapper.selectById(paymentId)).thenReturn(null);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderPaymentService.refundPayment(paymentId));
        assertEquals(ErrorCode.PAYMENT_NOT_FOUND.getCode(), 7001);
        verify(orderPaymentMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("退款 - RELEASED 状态抛出 PAYMENT_STATUS_INVALID")
    void testRefundPayment_ReleasedStatus() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setStatus(OrderPaymentStatusEnum.RELEASED);

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderPaymentService.refundPayment(paymentId));
        verify(orderPaymentMapper, never()).updateById(any());
    }

    // ======================== OrderPaymentServiceImpl: getOrderPayment ========================

    @Test
    @DisplayName("查询支付记录 - 存在返回 VO")
    void testGetOrderPayment_Found() {
        // Arrange
        Long paymentId = 1L;
        OrderPayment payment = new OrderPayment();
        payment.setId(paymentId);
        payment.setOrderId(100L);
        payment.setStatus(OrderPaymentStatusEnum.LOCKED);
        payment.setAmount(new BigDecimal("99.99"));

        when(orderPaymentMapper.selectById(paymentId)).thenReturn(payment);

        // Act
        var vo = orderPaymentService.getOrderPayment(paymentId);

        // Assert
        assertNotNull(vo);
        assertEquals(paymentId, vo.getId());
        assertEquals(100L, vo.getOrderId());
        verify(orderPaymentMapper).selectById(paymentId);
    }

    @Test
    @DisplayName("查询支付记录 - 不存在返回 null")
    void testGetOrderPayment_NotFound() {
        // Arrange
        Long paymentId = 999L;
        when(orderPaymentMapper.selectById(paymentId)).thenReturn(null);

        // Act
        var vo = orderPaymentService.getOrderPayment(paymentId);

        // Assert
        assertNull(vo);
        verify(orderPaymentMapper).selectById(paymentId);
    }
}
