package ru.gnivc.gatewayservice.filter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
public class RequestFilter implements WebFilter {
    private static final String COMPANY_NAME_QUERY_PARAM = "companyName";

    @Getter
    private Optional<String> companyNameFromRequest;

    public RequestFilter() {
        companyNameFromRequest = Optional.empty();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        companyNameFromRequest = Optional.ofNullable(exchange.getRequest()
                .getQueryParams()
                .toSingleValueMap()
                .get(COMPANY_NAME_QUERY_PARAM));

        log.info("company name (filter): {}", companyNameFromRequest);
        return chain.filter(exchange);
    }
}

