//package ru.gnivc.issueservice.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class ApplicationConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(request-> request
//                        .requestMatchers(HttpMethod.POST, "/someEndpoint")
//                        .hasAuthority("SCOPE_update_issue")
//                        .anyRequest().denyAll())
//                .csrf(CsrfConfigurer::disable)
//                // приложение не будет хранить состояние пользователя в сеансах HTTP
//                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .oauth2ResourceServer(oauth2ResourceServer-> oauth2ResourceServer.jwt(Customizer.withDefaults()))
//                .build();
//    }
//}
