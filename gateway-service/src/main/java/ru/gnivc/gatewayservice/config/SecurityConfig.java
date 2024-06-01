package ru.gnivc.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.authentication.ReactiveOidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
//import ru.gnivc.gatewayservice.filter.RequestModifyFilter;
import org.springframework.web.context.annotation.RequestScope;
import reactor.core.publisher.Mono;
//import ru.gnivc.gatewayservice.filter.RequestFilter;
import ru.gnivc.gatewayservice.util.Role;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
//    private final RequestFilter requestFilter;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http.httpBasic(withDefaults())
                .cors(withDefaults()) //возможность работать с проксированными HTTP-запросами
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(reactiveAuthenticationManager)
//                .addFilterBefore(requestFilter, SecurityWebFiltersOrder.FIRST)
                .authorizeExchange(requests -> {
                    requests.pathMatchers("openid-connect/**").permitAll();

                    //user
                    //новый юзер - REGISTRATOR
                    requests.pathMatchers(HttpMethod.POST, "portal/v1/user").permitAll();
                    //изменение своего аккаунта - здесь используется email пользователя
                    requests.pathMatchers(HttpMethod.PUT, "portal/v1/user").authenticated();
                    //запрос на сброс пароля - выслать на почту секретный код
                    requests.pathMatchers(HttpMethod.GET, "portal/v1/user/password/reset-request").permitAll();
                    //сброс пароля и высылка на почту нового - здесь ожидаю секретный код с почты
                    requests.pathMatchers(HttpMethod.POST, "portal/v1/user/password").permitAll();
                    //смена пароля - пароль в теле запроса
                    requests.pathMatchers(HttpMethod.PUT, "portal/v1/user/password").authenticated();

                    //company
                    //регистрация компании - здесь используется email пользователя
                    requests.pathMatchers(HttpMethod.POST, "portal/v1/company").hasRole(Role.REGISTRATOR.name());
                    //регистрация сотрудников компаний админом - все виды ролей
                    requests.pathMatchers(HttpMethod.POST, "portal/v1/company/admin/user").hasRole(Role.ADMIN.name());
                    //регистрация сотрудников компаний логистом - только водители
                    requests.pathMatchers(HttpMethod.POST, "portal/v1/company/logist/user").hasRole(Role.LOGIST.name());
                    //регистрация транспортного средства
                    requests.pathMatchers(HttpMethod.POST, "portal/v1/company/vehicle")
                            .hasAnyRole(Role.ADMIN.name(), Role.LOGIST.name());
                    //удаление компании
                    requests.pathMatchers(HttpMethod.DELETE, "portal/v1/company").hasRole(Role.ADMIN.name());
                    //карточка компании
                    requests.pathMatchers(HttpMethod.GET, "portal/v1/company").hasRole(Role.ADMIN.name());
                    //список компаний
                    requests.pathMatchers(HttpMethod.GET, "portal/v1/company/list").hasRole(Role.ADMIN.name());
                    //список доступных ролей в компаниях
                    requests.pathMatchers(HttpMethod.GET, "portal/v1/company/roles")
                            .hasAnyRole(Role.REGISTRATOR.name(), Role.ADMIN.name(), Role.LOGIST.name());
                    //список сотрудников компании
                    requests.pathMatchers(HttpMethod.GET, "portal/v1/company/users").hasRole(Role.ADMIN.name());
                })

//                .oauth2ResourceServer(resourceServerConfigurer -> resourceServerConfigurer
//                        .authenticationManagerResolver(context -> Mono.just(reactiveAuthenticationManager))
//                )
//                .oauth2ResourceServer((oauth2ResourceServer) ->
//                        oauth2ResourceServer
//                                .jwt(withDefaults()))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        )
                )
                .build();
    }


    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new GrantedAuthoritiesExtractor());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    static class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            List<String > realmRoles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
            Collection<GrantedAuthority> authorities = realmRoles.stream()
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();
            log.info("authorities: {}", authorities);
            return authorities;
        }
    }
}


