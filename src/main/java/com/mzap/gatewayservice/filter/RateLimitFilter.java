package com.mzap.gatewayservice.filter;

import com.mzap.gatewayservice.ratelimit.InMemoryRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    private final InMemoryRateLimiter limiter;
    @Value("${gateway.rateLimit.maxRequests}")
    private int maxRequests;

    public RateLimitFilter(InMemoryRateLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        var response = exchange.getResponse();
        var remoteAddress = request.getRemoteAddress();

        String ip = remoteAddress == null
                ? "null"
                : remoteAddress.getAddress().getHostAddress();

        boolean allowed = limiter.isAllowed(ip);

        response.getHeaders().add("X-RateLimit-Remaining", Integer.toString(limiter.remainingCalls(ip)));
        response.getHeaders().add("X-RateLimit-Limit", Integer.toString(maxRequests));
        response.getHeaders().add("X-Client-IP", ip);

        if(!allowed) {
            logger.warn("Rate Limiting ip: {}", ip);
            logger.warn("Request: {}", request);

            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
