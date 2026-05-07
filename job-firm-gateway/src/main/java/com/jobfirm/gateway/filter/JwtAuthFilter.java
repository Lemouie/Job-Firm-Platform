package com.jobfirm.gateway.filter;

import com.jobfirm.common.result.Result;
import com.jobfirm.common.auth.vo.TokenValidateVO;
import com.jobfirm.common.config.JobFirmProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, org.springframework.core.Ordered {

    private final WebClient.Builder webClientBuilder;
    private final JobFirmProperties jobFirmProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("test -5");
        String path = exchange.getRequest().getURI().getPath();

        // 放行 auth-service
        if (path.startsWith("/api/auth")) {
            System.out.println("test -4");
            return chain.filter(exchange);
        }

        // 放行登录、注册
        if (path.startsWith("/api/users/login") || path.startsWith("/api/users/register")) {
            System.out.println("test -3");
            return chain.filter(exchange);
        }

        // 其他请求必须带 Token
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        System.out.println("test -2");
        // 调用 auth-service 校验 Token
        return webClientBuilder.build()
                .post()
                // 不会走 Gateway Route，直接访问 auth-service，不得添加/api前缀
                .uri("lb://auth-service/auth/validate")
                .header("Authorization", "Bearer " + token)
                // 不会走 Gateway Route，直接访问 auth-service，要添加内部调用密钥
                .header("X-Internal-Secret", jobFirmProperties.getInternalSecret())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Result<TokenValidateVO>>() {})
//                .timeout(Duration.ofSeconds(3))
                .flatMap(result -> {

                    if (result == null || result.getCode() != 0 || result.getData() == null || !result.getData().getValid()) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    TokenValidateVO vo = result.getData();
                    if (vo.getNewToken() != null) {
                        exchange.getResponse().getHeaders().add("X-New-Token", vo.getNewToken());
                    }

                    ServerHttpRequest newRequest = exchange.getRequest().mutate()
                            .headers(headers -> {
                                headers.remove("X-User-Id");
                                headers.remove("X-User-Role");

                                headers.add("X-User-Id", String.valueOf(vo.getUserId()));
                                headers.add("X-User-Role", vo.getRole());
                            })
                            .build();

                    ServerWebExchange newExchange = exchange.mutate()
                            .request(newRequest)
                            .build();

                    System.out.println("test -1");
                    return chain.filter(newExchange);
                })
                .onErrorResume(e -> {
                    e.printStackTrace();
                    exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                    return exchange.getResponse().setComplete();
                });
    }


    @Override
    public int getOrder() {
        return -100; // 优先级高
    }
}
