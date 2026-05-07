package com.jobfirm.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelBlockExceptionHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Gateway 级 Sentinel 限流配置
 * <p>
 * 为 API 网关各路由提供细粒度的限流控制。
 * 使用 Sentinel Gateway API 定义分组和流控规则。
 */
@Slf4j
@Configuration
public class SentinelGatewayConfig {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public SentinelGatewayConfig(
            ObjectProvider<List<ViewResolver>> viewResolversProvider,
            ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 注册 Sentinel 全局过滤器，拦截 Gateway 请求进行限流判断
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * 自定义限流异常处理，返回 JSON 格式的响应
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelBlockExceptionHandler sentinelBlockExceptionHandler() {
        return new SentinelBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /**
     * 初始化网关 API 分组和限流规则
     */
    @PostConstruct
    public void initGatewayRules() {
        log.info("[Sentinel Gateway] 开始初始化网关限流规则...");
        initCustomizedApis();
        initGatewayFlowRules();
        log.info("[Sentinel Gateway] 网关限流规则初始化完成");
    }

    /**
     * 定义 API 分组
     */
    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new java.util.HashSet<>();

        // Auth API 分组
        ApiDefinition authApi = new ApiDefinition("auth_api")
                .setPredicateItems(Collections.singletonList(
                        new ApiPathPredicateItem()
                                .setPattern("/api/auth/**")
                                .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX)
                ));
        definitions.add(authApi);

        // Order API 分组
        ApiDefinition orderApi = new ApiDefinition("order_api")
                .setPredicateItems(Collections.singletonList(
                        new ApiPathPredicateItem()
                                .setPattern("/api/order/**")
                                .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX)
                ));
        definitions.add(orderApi);

        // Payment API 分组
        ApiDefinition paymentApi = new ApiDefinition("payment_api")
                .setPredicateItems(Collections.singletonList(
                        new ApiPathPredicateItem()
                                .setPattern("/api/payment/**")
                                .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX)
                ));
        definitions.add(paymentApi);

        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
        log.info("[Sentinel Gateway] API 分组已加载: {} 个", definitions.size());
    }

    /**
     * 定义网关流控规则
     */
    private void initGatewayFlowRules() {
        Set<GatewayFlowRule> rules = new java.util.HashSet<>();

        // Auth 服务限流: 整体 QPS 100
        rules.add(new GatewayFlowRule("auth_api")
                .setCount(100)
                .setIntervalSec(1));

        // Order 服务限流: 整体 QPS 300
        rules.add(new GatewayFlowRule("order_api")
                .setCount(300)
                .setIntervalSec(1));

        // Payment 服务限流: 整体 QPS 200
        rules.add(new GatewayFlowRule("payment_api")
                .setCount(200)
                .setIntervalSec(1));

        GatewayRuleManager.loadRules(rules);
        log.info("[Sentinel Gateway] 流控规则已加载: {} 条", rules.size());
    }
}
