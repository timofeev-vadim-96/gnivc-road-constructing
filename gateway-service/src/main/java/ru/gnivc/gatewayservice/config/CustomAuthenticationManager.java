package ru.gnivc.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }

        return Mono.empty();
    }
}