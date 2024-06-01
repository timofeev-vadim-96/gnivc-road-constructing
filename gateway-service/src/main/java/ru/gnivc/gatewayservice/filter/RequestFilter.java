//package ru.gnivc.gatewayservice.filter;
//
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.util.Optional;
//
//@Component
//@Slf4j
//public class RequestFilter implements WebFilter {
//
//    private final ReactiveJwtDecoder jwtDecoder;
//    @Getter
//    private Optional<String> companyNameFromRequest;
//    private static final String COMPANY_NAME_QUERY_PARAM = "companyName";
//    private static final String EMAIL_QUERY_PARAM = "email";
//
//    public RequestFilter(ReactiveJwtDecoder jwtDecoder) {
//        this.jwtDecoder = jwtDecoder;
//        companyNameFromRequest = Optional.empty();
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        companyNameFromRequest = Optional.ofNullable(exchange.getRequest()
//                .getQueryParams()
//                .toSingleValueMap()
//                .get(COMPANY_NAME_QUERY_PARAM));
//        log.info("company name (filter): {}", companyNameFromRequest);
//        return chain.filter(exchange);
//    }
//}
//
