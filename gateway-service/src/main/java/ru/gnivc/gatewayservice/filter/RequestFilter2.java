//package ru.gnivc.gatewayservice.filter;
//
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContext;
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
//public class RequestFilter2 implements WebFilter {
//
//    private final ReactiveJwtDecoder jwtDecoder;
//    @Getter
//    private Optional<String> companyNameFromRequest;
//    private static final String COMPANY_NAME_QUERY_PARAM = "companyName";
//    private static final String EMAIL_QUERY_PARAM = "email";
//
//    public RequestFilter2(ReactiveJwtDecoder jwtDecoder) {
//        this.jwtDecoder = jwtDecoder;
//        companyNameFromRequest = Optional.empty();
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        return ReactiveSecurityContextHolder.getContext()
//                .map(SecurityContext::getAuthentication)
//                .flatMap(authentication -> {
//                    if (authentication != null) {
//                        String token = (String) authentication.getCredentials();
//
//                        return jwtDecoder.decode(token)
//                                .flatMap(jwt -> {
//                                    String email = jwt.getClaimAsString(EMAIL_QUERY_PARAM);
//
//                                    ServerHttpRequest request = exchange.getRequest();
//                                    request.getQueryParams().add(EMAIL_QUERY_PARAM, email);
//
//                                    companyNameFromRequest = request.getQueryParams()
//                                            .get(COMPANY_NAME_QUERY_PARAM)
//                                            .stream()
//                                            .findFirst();
//
//                                    log.info("company name in current request: {}", companyNameFromRequest);
//                                    log.info("Request after adding email: {}", request);
//
//                                    // Продолжаем обработку запроса
//                                    return chain.filter(exchange.mutate().request(request).build());
//                                });
//                    } else {
//                        return chain.filter(exchange);
//                    }
//                });
//    }
//}
//
