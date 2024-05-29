package ru.gnivc.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.gnivc.gatewayservice.util.ReactiveRequestContextHolder;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtDecoder jwtDecoder;

    /**
     * A method for getting the company id if it is present in the url
     */
    private Optional<String> getCompanyId() {
        ServerHttpRequest httpRequest = ReactiveRequestContextHolder.getRequest().block();
        return Optional.ofNullable(Objects.requireNonNull(httpRequest).getQueryParams().getFirst("clientId"));
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        Optional<String> companyId = getCompanyId();
        String token = (String) authentication.getCredentials();

        Mono<Jwt> jwtMono = Mono.fromCallable(() -> jwtDecoder.decode(token));

        return jwtMono
                .map(jwt -> {
                    Collection<String> realmRoles = jwt.getClaimAsStringList("realm_access.roles");
                    Collection<GrantedAuthority> realmAuthorities = realmRoles.stream()
                            .filter(role -> role.startsWith("ROLE_"))
                            .map(SimpleGrantedAuthority::new)
                            .map(GrantedAuthority.class::cast)
                            .toList();

                    //если в запросе присутствует companyId - возвращаем роли реалма + роли клиента
                    if (companyId.isPresent()) {
                        Collection<String> clientRoles = jwt.getClaimAsStringList("resource_access." + companyId + ".roles");
                        Collection<GrantedAuthority> clientAuthorities = clientRoles.stream()
                                .filter(role -> role.startsWith("ROLE_"))
                                .map(SimpleGrantedAuthority::new)
                                .map(GrantedAuthority.class::cast)
                                .toList();

                        Collection<GrantedAuthority> combinedAuthorities = Stream.concat(realmAuthorities.stream(), clientAuthorities.stream())
                                .collect(Collectors.toSet());
                        return new UsernamePasswordAuthenticationToken(
                                jwt.getClaimAsString("preferred_username"),
                                null,
                                combinedAuthorities);
                        //если в запросе нет companyId - возвращаем роли реалма
                    } else return new UsernamePasswordAuthenticationToken(
                            jwt.getClaimAsString("preferred_username"),
                            null,
                            realmAuthorities);
                });
    }
}