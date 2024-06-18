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

                    protectEndpoints(requests);
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        )
                )
                .build();
    }

    private void protectEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec requests) {
        protectUserEndpoints(requests);

        protectCompanyEndpoints(requests);

        protectVehicleEndpoints(requests);

        protectTaskEndpoints(requests);

        protectTripEndpoints(requests);

        protectDriverEndpoints(requests);

        protectImageEndpoints(requests);

        protectDwhEndpoints(requests);

        requests.anyExchange().denyAll();
    }

    private void protectDwhEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec requests) {
        requests.pathMatchers(HttpMethod.GET, "dwh/v1/statistics").hasRole(Role.ADMIN.name());
    }

    private void protectImageEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec requests) {
        requests.pathMatchers(HttpMethod.POST, "driver/v1/image").hasRole(Role.DRIVER.name());
        requests.pathMatchers(HttpMethod.GET, "driver/v1/image").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.DELETE, "driver/v1/image").hasRole(Role.LOGIST.name());
    }

    private void protectDriverEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec requests) {
        requests.pathMatchers(HttpMethod.GET, "driver/v1/task/{driverId}").hasRole(Role.DRIVER.name());
        requests.pathMatchers(HttpMethod.POST, "driver/v1/trip").hasRole(Role.DRIVER.name());
        requests.pathMatchers(HttpMethod.POST, "driver/v1/location").hasRole(Role.DRIVER.name());
        requests.pathMatchers(HttpMethod.POST, "driver/v1/event").hasRole(Role.DRIVER.name());
    }

    private void protectTripEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec requests) {
        requests.pathMatchers(HttpMethod.GET, "/logist/v1/trip/{tripId}").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.GET, "/logist/v1/trip/list").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.GET, "/logist/v1/trip/{tripId}/events").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.GET, "/logist/v1/trip/{tripId}/locations").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.POST, "/logist/v1/trip").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.DELETE, "/logist/v1/trip/{tripId}").hasRole(Role.LOGIST.name());
    }

    private void protectTaskEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec requests) {
        requests.pathMatchers(HttpMethod.POST, "/logist/v1/task").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.GET, "/logist/v1/task/{taskId}").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.DELETE, "/logist/v1/task/{taskId}").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.GET, "/logist/v1/task/list").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.GET, "/logist/v1/task/byDriver/{driverId}").hasRole(Role.LOGIST.name());
    }

    private void protectVehicleEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec requests) {
        requests.pathMatchers(HttpMethod.POST, "portal/v1/company/vehicle")
                .hasAnyRole(Role.ADMIN.name(), Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.DELETE, "portal/v1/company/vehicle/{vehicleId}")
                .hasAnyRole(Role.ADMIN.name(), Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.GET, "portal/v1/company/vehicle/{vehicleId}")
                .hasAnyRole(Role.ADMIN.name(), Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.GET, "portal/v1/company/vehicle/list")
                .hasAnyRole(Role.ADMIN.name(), Role.LOGIST.name());
    }

    private void protectCompanyEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec requests) {
        requests.pathMatchers(HttpMethod.POST, "portal/v1/company").hasRole(Role.REGISTRATOR.name());
        requests.pathMatchers(HttpMethod.PUT, "portal/v1/company").hasRole(Role.ADMIN.name());
        requests.pathMatchers(HttpMethod.POST, "portal/v1/company/admin/user").hasRole(Role.ADMIN.name());
        requests.pathMatchers(HttpMethod.POST, "portal/v1/company/logist/user").hasRole(Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.DELETE, "portal/v1/company").hasRole(Role.ADMIN.name());
        requests.pathMatchers(HttpMethod.GET, "portal/v1/company").hasRole(Role.ADMIN.name());
        requests.pathMatchers(HttpMethod.GET, "portal/v1/company/list").hasRole(Role.ADMIN.name());
        requests.pathMatchers(HttpMethod.GET, "portal/v1/company/roles")
                .hasAnyRole(Role.REGISTRATOR.name(), Role.ADMIN.name(), Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.GET, "portal/v1/company/users").hasRole(Role.ADMIN.name());
    }

    private void protectUserEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec requests) {
        requests.pathMatchers(HttpMethod.POST, "portal/v1/user").permitAll();
        requests.pathMatchers(HttpMethod.PUT, "portal/v1/user").authenticated();
        requests.pathMatchers(HttpMethod.GET, "portal/v1/user/{userId}")
                .hasAnyRole(Role.ADMIN.name(), Role.LOGIST.name());
        requests.pathMatchers(HttpMethod.DELETE, "portal/v1/user/{userId}").hasRole(Role.ADMIN.name());
        requests.pathMatchers(HttpMethod.GET, "portal/v1/user/password/reset-request").permitAll();
        requests.pathMatchers(HttpMethod.POST, "portal/v1/user/password").permitAll();
        requests.pathMatchers(HttpMethod.PUT, "portal/v1/user/password").authenticated();
    }

    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter
                .setJwtGrantedAuthoritiesConverter(new CustomGrantedAuthoritiesExtractor(requestFilter));
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}


