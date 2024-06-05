package ru.gnivc.gatewayservice.filter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class RequestFilter implements WebFilter {

    @Getter
    private Optional<String> companyNameFromRequest;
//    private ServerHttpRequest httpRequest;
    private static final String COMPANY_NAME_QUERY_PARAM = "companyName";
//    private static final String EMAIL_QUERY_PARAM = "email";

    public RequestFilter() {
        companyNameFromRequest = Optional.empty();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        httpRequest = exchange.getRequest();

        companyNameFromRequest = Optional.ofNullable(exchange.getRequest()
                .getQueryParams()
                .toSingleValueMap()
                .get(COMPANY_NAME_QUERY_PARAM));

        log.info("company name (filter): {}", companyNameFromRequest);
        return chain.filter(exchange);
    }

//    /**
//     * Changes the email in the request to the user's email from the token in the case when access to the
//     * endpoint is carried out by his email
//     */
//    public void overrideUsersEmail(String emailFromToken) {
//        if (httpRequest.getURI().toString().contains("/portal/v1/user") && httpRequest.getMethod().equals(HttpMethod.PUT)) {
//            Optional<String> emailFromRequest = Optional.ofNullable(httpRequest
//                    .getQueryParams()
//                    .toSingleValueMap()
//                    .get(EMAIL_QUERY_PARAM));
//            log.info("email param from request: {}", emailFromRequest);
//            if (emailFromRequest.isPresent() && !emailFromToken.equals(emailFromRequest.get())){
//                httpRequest.getQueryParams().set(EMAIL_QUERY_PARAM, emailFromToken);
//                log.warn("User's email was rewritten with request filter due to the fact that the requested resource requires the " +
//                        "email value of the current user");
//            }
//        }
//    }
}

