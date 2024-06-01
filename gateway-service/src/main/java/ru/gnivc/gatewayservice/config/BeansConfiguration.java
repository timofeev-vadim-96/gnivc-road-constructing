//package ru.gnivc.gatewayservice.config;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;
//import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
//import org.springframework.web.context.annotation.RequestScope;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import ru.gnivc.gatewayservice.filter.RequestFilter;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Configuration
//@Slf4j
//@RequiredArgsConstructor
//public class BeansConfiguration {
//    private final RequestFilter requestFilter;
//
//    @Bean
//    @RequestScope
//    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter(){
//        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
//        Optional<String> companyName = requestFilter.getCompanyNameFromRequest();
//        converter.setPrincipalClaimName("email");
//        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            List<String > realmRoles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
//            List<GrantedAuthority> realmAuthorities = realmRoles.stream()
//                    .filter(role -> role.startsWith("ROLE_"))
//                    .map(SimpleGrantedAuthority::new)
//                    .map(GrantedAuthority.class::cast)
//                    .toList();
//
//                // Если в запросе присутствует companyId - возвращаем роли реалма + роли клиента
//                if (companyName.isPresent()) {
//                    Map<String, Object> resourceAccessClaims = jwt.getClaimAsMap("resource_access");
//                    Map<String, Object> clientClaims = (Map<String, Object>) resourceAccessClaims.get(companyName);
//                    List<String> clientRoles = (List<String>) clientClaims.get("roles");
//                    List<GrantedAuthority> clientAuthorities = clientRoles.stream()
//                            .filter(role -> role.startsWith("ROLE_"))
//                            .map(SimpleGrantedAuthority::new)
//                            .map(GrantedAuthority.class::cast)
//                            .toList();
//
//                    List<GrantedAuthority> combinedAuthorities = Stream.concat(realmAuthorities.stream(), clientAuthorities.stream())
//                            .toList();
//                    log.info("Current user's roles (client branch): {}", combinedAuthorities);
//                    return combinedAuthorities;
//                } else { // Если в запросе нет companyId - возвращаем роли реалма
//                    log.info("Current user's roles (realm branch): {}", realmAuthorities);
//                    return realmAuthorities;
//                }
//            });
//        });
//        return converter;
//}
