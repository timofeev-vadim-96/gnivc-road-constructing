package ru.gnivc.gatewayservice.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

/**
 * A class for getting the reactive static context of an HTTP request
 */
public class ReactiveRequestContextHolder {
    public static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(ctx ->
                Mono.just(ctx.get(CONTEXT_KEY)));
    }
}
