package ru.gnivc.gatewayservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import ru.gnivc.gatewayservice.filter.RequestFilter;
import ru.gnivc.gatewayservice.util.Role;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final RequestFilter requestFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http.httpBasic(withDefaults())
                .cors(withDefaults()) //возможность работать с проксированными HTTP-запросами
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(reactiveAuthenticationManager)
                .addFilterBefore(requestFilter, SecurityWebFiltersOrder.HTTP_HEADERS_WRITER)
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
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        )
                )
                .build();
    }

    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new CustomGrantedAuthoritiesExtractor(requestFilter));
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}


