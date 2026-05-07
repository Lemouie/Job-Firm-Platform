package com.jobfirm.common.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.common.result.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * Sentinel 通用限流降级处理器
 * <p>
 * 提供统一的 BlockException 处理和 Fallback 处理，供各业务模块引用。
 * 在 @SentinelResource 注解中通过 blockHandlerClass / fallbackClass 指定。
 */
@Slf4j
public class BlockHandler {

    /**
     * 统一的限流/降级异常处理入口
     * <p>
     * 方法签名必须为：static 返回类型 方法名(参数列表, BlockException)
     */
    public static <T> Result<T> handleBlockException(Object obj, BlockException e) {
        log.warn("[Sentinel] blocked invocation: resource={} type={} message={}",
                obj != null ? obj.getClass().getSimpleName() : "unknown",
                e.getClass().getSimpleName(),
                e.getMessage());

        if (e instanceof FlowException) {
            return Result.fail(ErrorCode.RATE_LIMITED.getCode(), ErrorCode.RATE_LIMITED.getMessage());
        }
        if (e instanceof DegradeException) {
            return Result.fail(ErrorCode.SERVICE_UNAVAILABLE.getCode(),
                    "服务熔断降级，请稍后重试");
        }
        if (e instanceof AuthorityException) {
            return Result.fail(ErrorCode.FORBIDDEN.getCode(), "访问被拒绝");
        }
        return Result.fail(ErrorCode.RATE_LIMITED.getCode(), "请求被限流");
    }

    /**
     * 统一的 Fallback 处理（降级逻辑）
     * <p>
     * 当 @SentinelResource 标注的方法抛出异常时触发（非 BlockException）。
     * 方法签名必须为：static 返回类型 方法名(参数列表, Throwable)
     */
    public static <T> Result<T> handleFallback(Object obj, Throwable t) {
        log.error("[Sentinel] fallback invoked: resource={} error={}",
                obj != null ? obj.getClass().getSimpleName() : "unknown",
                t.getMessage(), t);
        return Result.fail(ErrorCode.SERVICE_UNAVAILABLE.getCode(),
                "服务繁忙，请稍后重试");
    }
}
