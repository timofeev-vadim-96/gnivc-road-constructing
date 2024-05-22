package ru.gnivc.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import ru.gnivc.gatewayservice.util.Role;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.httpBasic(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(requests -> {
                    requests.pathMatchers("openid-connect/**").permitAll();

                    //user
                    requests.pathMatchers(HttpMethod.POST, "portal/v1/user").permitAll();
                    requests.pathMatchers(HttpMethod.GET, "portal/v1/password/reset-request/{login}").permitAll();
                    requests.pathMatchers(HttpMethod.POST, "portal/v1/password").permitAll();
                    requests.pathMatchers(HttpMethod.PUT, "portal/v1/password").authenticated();
                    //company
                    requests.pathMatchers(HttpMethod.POST, "portal/v1/company").hasRole(Role.REGISTRATOR.toString());
                    requests.pathMatchers(HttpMethod.PUT, "portal/v1/company").hasRole(Role.REGISTRATOR.toString());
                    requests.pathMatchers(HttpMethod.DELETE, "portal/v1/company").hasRole(Role.REGISTRATOR.toString());
                    requests.pathMatchers(HttpMethod.GET, "portal/v1/company/{id}").hasAnyRole(Role.REGISTRATOR.toString(), Role.ADMIN.toString(), Role.LOGIST.toString());
                })
                .build();
    }
}
