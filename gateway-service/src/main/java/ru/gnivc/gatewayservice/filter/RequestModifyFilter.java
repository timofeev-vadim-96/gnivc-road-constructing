package ru.gnivc.gatewayservice.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RequestModifyFilter implements WebFilter {
    private final JwtDecoder jwtDecoder;
    private final Authentication authentication;

    public RequestModifyFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
        authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (authentication != null) {
            request.getQueryParams().add("email", getUserId());
        }

        return chain.filter(exchange.mutate()
                .request(request)
                .build());
    }

    private String getUserId() {
        String token = (String) authentication.getCredentials();
        Mono<Jwt> jwtMono = Mono.fromCallable(() -> jwtDecoder.decode(token));
        return jwtMono.map(jwt -> jwt.getClaimAsString("email")).block();
    }
}
