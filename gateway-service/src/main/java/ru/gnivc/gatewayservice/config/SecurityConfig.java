package ru.gnivc.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http.httpBasic(withDefaults())
                .cors(withDefaults()) //возможность работать с проксированными HTTP-запросами
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(reactiveAuthenticationManager())
                .authorizeExchange(requests -> {
                    requests.pathMatchers("openid-connect/**").permitAll();
                    requests.anyExchange().authenticated();
                })
                .oauth2ResourceServer((oauth2ResourceServer) ->
                        oauth2ResourceServer
                                .jwt(withDefaults())
                )
                .build();
    }
                    //2 НЕДЕЛЯ
//                    //user
//                    requests.pathMatchers(HttpMethod.POST, "portal/v1/user").permitAll();
//                    requests.pathMatchers(HttpMethod.GET, "portal/v1/password/reset-request/{login}").permitAll();
//                    requests.pathMatchers(HttpMethod.POST, "portal/v1/password").permitAll();
//                    requests.pathMatchers(HttpMethod.PUT, "portal/v1/password").authenticated();
//                    //company
//                    requests.pathMatchers(HttpMethod.POST, "portal/v1/company").authenticated();
//                    requests.pathMatchers(HttpMethod.PUT, "portal/v1/company").authenticated();
//                    requests.pathMatchers(HttpMethod.DELETE, "portal/v1/company").authenticated();
//                    requests.pathMatchers(HttpMethod.GET, "portal/v1/company/{id}").authenticated();

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(){
        return new CustomAuthenticationManager();
    }
}
