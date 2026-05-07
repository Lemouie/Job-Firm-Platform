package com.jobfirm.infrastructure.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 规则配置
 * <p>
 * 通过 @PostConstruct 在应用启动时初始化流控、熔断降级和授权规则。
 * 各规则的 resource 名称与 @SentinelResource(value = "...") 对应。
 * 生产环境下建议通过 Sentinel Dashboard 或 Nacos 动态配置。
 */
@Slf4j
@Configuration
public class SentinelRulesConfig {

    // ======================== 资源名常量定义 ========================

    /** Order Service - 支付订单 */
    public static final String RES_ORDER_PAY = "order-service:payOrder";
    /** Order Service - 创建订单 */
    public static final String RES_ORDER_CREATE = "order-service:createOrder";

    /** Payment Service - 创建支付 */
    public static final String RES_PAYMENT_CREATE = "payment-service:createOrderPayment";
    /** Payment Service - 支付回调 */
    public static final String RES_PAYMENT_CALLBACK = "payment-service:callback";

    /** Auth Service - 登录 */
    public static final String RES_AUTH_LOGIN = "auth-service:login";
    /** Auth Service - 注册 */
    public static final String RES_AUTH_REGISTER = "auth-service:register";

    @PostConstruct
    public void init() {
        log.info("[Sentinel] 开始初始化规则...");
        initFlowRules();
        initDegradeRules();
        initAuthorityRules();
        log.info("[Sentinel] 规则初始化完成");
    }

    // ======================== 流控规则 (Flow) ========================

    private void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // ----- Order Service -----
        // payOrder: QPS 100
        rules.add(buildFlowRule(RES_ORDER_PAY, 100, RuleConstant.FLOW_GRADE_QPS,
                RuleConstant.CONTROL_BEHAVIOR_DEFAULT));
        // createOrder: QPS 200
        rules.add(buildFlowRule(RES_ORDER_CREATE, 200, RuleConstant.FLOW_GRADE_QPS,
                RuleConstant.CONTROL_BEHAVIOR_DEFAULT));

        // ----- Payment Service -----
        // createOrderPayment: QPS 100
        rules.add(buildFlowRule(RES_PAYMENT_CREATE, 100, RuleConstant.FLOW_GRADE_QPS,
                RuleConstant.CONTROL_BEHAVIOR_DEFAULT));
        // callback: QPS 200 (回调可能有重试，门槛略高)
        rules.add(buildFlowRule(RES_PAYMENT_CALLBACK, 200, RuleConstant.FLOW_GRADE_QPS,
                RuleConstant.CONTROL_BEHAVIOR_DEFAULT));

        // ----- Auth Service -----
        // login: QPS 50
        rules.add(buildFlowRule(RES_AUTH_LOGIN, 50, RuleConstant.FLOW_GRADE_QPS,
                RuleConstant.CONTROL_BEHAVIOR_DEFAULT));
        // register: QPS 30 (注册比登录频率低)
        rules.add(buildFlowRule(RES_AUTH_REGISTER, 30, RuleConstant.FLOW_GRADE_QPS,
                RuleConstant.CONTROL_BEHAVIOR_DEFAULT));

        FlowRuleManager.loadRules(rules);
        log.info("[Sentinel] 流控规则已加载: {} 条", rules.size());
    }

    // ======================== 熔断降级规则 (Degrade) ========================

    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // Order Service - payOrder: 慢调用比例熔断
        // RT > 500ms 且比例 >= 50% 时熔断，熔断持续时间 10s
        rules.add(buildDegradeRule(RES_ORDER_PAY,
                RuleConstant.DEGRADE_GRADE_RT, 500, 0.5, 10));

        // Payment Service - callback: 异常比例熔断
        // 异常比例 >= 30% 时熔断，熔断持续时间 10s
        rules.add(buildDegradeRule(RES_PAYMENT_CALLBACK,
                RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO, 0.3, 0.3, 10));

        // Auth Service - login: 异常比例熔断
        // 异常比例 >= 50% 时熔断，熔断持续时间 5s
        rules.add(buildDegradeRule(RES_AUTH_LOGIN,
                RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO, 0.5, 0.5, 5));

        DegradeRuleManager.loadRules(rules);
        log.info("[Sentinel] 熔断降级规则已加载: {} 条", rules.size());
    }

    // ======================== 授权规则 (Authority) ========================

    private void initAuthorityRules() {
        List<AuthorityRule> rules = new ArrayList<>();

        // 支付回调只允许来自内部网关或特定来源
        AuthorityRule callbackRule = new AuthorityRule();
        callbackRule.setResource(RES_PAYMENT_CALLBACK);
        callbackRule.setLimitApp("gateway,payment-service");
        callbackRule.setStrategy(RuleConstant.AUTHORITY_WHITE);
        rules.add(callbackRule);

        // 白名单示例: 允许内部服务调用订单创建
        AuthorityRule orderCreateRule = new AuthorityRule();
        orderCreateRule.setResource(RES_ORDER_CREATE);
        orderCreateRule.setLimitApp("gateway,order-service,auth-service");
        orderCreateRule.setStrategy(RuleConstant.AUTHORITY_WHITE);
        rules.add(orderCreateRule);

        AuthorityRuleManager.loadRules(rules);
        log.info("[Sentinel] 授权规则已加载: {} 条", rules.size());
    }

    // ======================== 构建器工具方法 ========================

    /**
     * 构建流控规则
     */
    private FlowRule buildFlowRule(String resource, double count, int grade, int controlBehavior) {
        FlowRule rule = new FlowRule();
        rule.setResource(resource);
        rule.setCount(count);
        rule.setGrade(grade);
        rule.setControlBehavior(controlBehavior);
        return rule;
    }

    /**
     * 构建熔断降级规则
     *
     * @param grade        降级策略: DEGRADE_GRADE_RT / DEGRADE_GRADE_EXCEPTION_RATIO / DEGRADE_GRADE_EXCEPTION_COUNT
     * @param count        阈值: RT(ms) 或 异常比例(0~1) 或 异常数
     * @param slowRatio    慢调用比例阈值 (仅 RT 模式有效, 其他模式可传 0)
     * @param minRequest   最小请求数 (仅异常比例模式，count 复用此参数)
     * @param timeWindow   熔断持续时间(秒)
     */
    private DegradeRule buildDegradeRule(String resource, int grade, double count,
                                          double slowRatio, int timeWindow) {
        DegradeRule rule = new DegradeRule();
        rule.setResource(resource);
        rule.setGrade(grade);
        rule.setCount(count);
        rule.setTimeWindow(timeWindow);

        if (grade == RuleConstant.DEGRADE_GRADE_RT) {
            // 慢调用比例模式下，设置 RT 阈值和比例阈值
            rule.setSlowRatioThreshold(slowRatio);
            rule.setMinRequestAmount(5);
            rule.setStatIntervalMs(1000);
        } else if (grade == RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO) {
            // 异常比例模式
            rule.setMinRequestAmount(5);
            rule.setStatIntervalMs(1000);
        }

        return rule;
    }
}
