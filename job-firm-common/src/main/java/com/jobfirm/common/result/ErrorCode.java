package com.jobfirm.common.result;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(0, "success"),
    FAIL(1, "fail"),
    PARAM_ERROR(1001, "参数错误"),

    // User (2xxx)
    ALREADY_REGISTER(2001, "手机号或邮箱已被注册"),
    LOGIN_NAME_OR_PASSWORD_ERROR(2002, "登录名或密码错误"),
    USER_DISABLED(2003, "用户已禁用"),
    USER_NOT_FOUND(2004, "用户不存在"),

    // Auth (3xxx)
    UNAUTHORIZED(3001, "未登录"),
    FORBIDDEN(3002, "无权限"),
    TOKEN_EXPIRED(3003, "Token已过期"),
    TOKEN_INVALID(3004, "Token无效"),

    // Firm (4xxx)
    FIRM_NOT_FOUND(4001, "事务所不存在"),
    FIRM_ALREADY_EXISTS(4002, "该CEO已创建事务所"),
    FIRM_NOT_APPROVED(4003, "事务所未通过审核"),
    FIRM_DISABLED(4004, "事务所已被禁用"),

    // Job (5xxx)
    JOB_NOT_FOUND(5001, "差事不存在"),
    JOB_NOT_PUBLISHED(5002, "差事未上架"),
    JOB_IMAGE_LIMIT(5003, "图片数量超过限制(最多9张)"),

    // Order (6xxx)
    ORDER_NOT_FOUND(6001, "订单不存在"),
    ORDER_STATUS_INVALID(6002, "订单状态不允许此操作"),
    ORDER_CANNOT_CANCEL(6003, "订单已进入执行阶段，不允许取消"),
    ORDER_NOT_PAID(6004, "订单未支付"),

    // Payment (7xxx)
    PAYMENT_NOT_FOUND(7001, "支付记录不存在"),
    PAYMENT_STATUS_INVALID(7002, "支付状态不允许此操作"),
    PAYMENT_FAILED(7003, "支付失败"),
    FIRM_INSUFFICIENT_BALANCE(7004, "事务所余额不足"),

    // System (9xxx)
    SYSTEM_ERROR(5000, "系统异常"),
    SERVICE_UNAVAILABLE(5001, "服务暂不可用"),
    RATE_LIMITED(5002, "请求过于频繁");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
