package ru.gnivc.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomAuthenticationManager2 implements ReactiveAuthenticationManager {
    private final ReactiveJwtDecoder reactiveJwtDecoder;

    /**
     * A method for getting the company id if it is present in the url
     */
    private Mono<Optional<String>> getCompanyName() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getCredentials)
                .cast(ServerWebExchange.class)
                .map(exchange -> Optional.ofNullable(exchange.getRequest()
                        .getQueryParams()
                        .toSingleValueMap()
                        .get("companyName")
                ));
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        Mono<String> companyName = getCompanyName()
                .map(Optional::get);

        Mono<Jwt> jwtMono = reactiveJwtDecoder.decode((String) authentication.getCredentials());

        return jwtMono.flatMap(jwt -> {
            List<String> realmRoles = getRealmRoles(jwt);
            Set<GrantedAuthority> realmAuthorities = realmRoles.stream()
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
            log.info("realm roles: " + realmAuthorities);

            // Используем flatMap для обработки companyNameMono
            return companyName.map(name -> {
                System.out.println("зашли внутрь");
                // Если в запросе присутствует companyId - возвращаем роли реалма + роли клиента
                if (!name.isEmpty()) {
                    System.out.println("есть name");
                    List<String> clientRoles = getClientRoles(jwt, name);
                    Set<GrantedAuthority> clientAuthorities = clientRoles.stream()
                            .filter(role -> role.startsWith("ROLE_"))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());

                    Set<GrantedAuthority> combinedAuthorities = Stream.concat(realmAuthorities.stream(), clientAuthorities.stream())
                            .collect(Collectors.toSet());
                    log.info("Current user's roles (client branch): {}", combinedAuthorities);
                    return new UsersRoles(combinedAuthorities);
                } else { // Если в запросе нет companyId - возвращаем роли реалма
                    log.info("Current user's roles (realm branch): {}", realmAuthorities);
                    return new UsersRoles(realmAuthorities);
                }
            });
        });
    }

    private static List<String> getRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccessClaims = jwt.getClaimAsMap("realm_access");
        List<String> realmRoles = (List<String>) realmAccessClaims.get("roles");
        return realmRoles;
    }

    private static List<String> getClientRoles(Jwt jwt, String name) {
        Map<String, Object> resourceAccessClaims = jwt.getClaimAsMap("resource_access");
        Map<String, Object> clientClaims = (Map<String, Object>) resourceAccessClaims.get(name);
        List<String> clientRoles = (List<String>) clientClaims.get("roles");
        return clientRoles;
    }

//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) {
//        Mono<Optional<String>> companyNameFromRequest = getCompanyName();
//        AtomicReference<Optional<String>> companyName = new AtomicReference<>();
//        companyNameFromRequest.subscribe(companyName::set);
//
//        //todo remove
//        log.info("Current company_id in the request: {}", companyName.get());
//        String token = (String) authentication.getCredentials();
//
//        Mono<Jwt> jwtMono = Mono.fromCallable(() -> jwtDecoder.decode(token));
//
//        return jwtMono
//                .map(jwt -> {
//                    List<String> realmRoles = jwt.getClaimAsStringList(REALM_ROLES_CLAIM);
//                    Set<GrantedAuthority> realmAuthorities = realmRoles.stream()
//                            .filter(role -> role.startsWith("ROLE_"))
//                            .map(SimpleGrantedAuthority::new)
//                            .collect(Collectors.toSet());
//
//                    //если в запросе присутствует companyId - возвращаем роли реалма + роли клиента
//                    if (companyName.get().isPresent()) {
//                        List<String> clientRoles = jwt.getClaimAsStringList("resource_access." + companyName + ".roles");
//                        Set<GrantedAuthority> clientAuthorities = clientRoles.stream()
//                                .filter(role -> role.startsWith("ROLE_"))
//                                .map(SimpleGrantedAuthority::new)
//                                .collect(Collectors.toSet());
//
//                        Set<GrantedAuthority> combinedAuthorities = Stream.concat(realmAuthorities.stream(), clientAuthorities.stream())
//                                .collect(Collectors.toSet());
//                        log.info("Current user's roles (client branch): {}", combinedAuthorities);
//                        //todo remove
//                        System.out.println(combinedAuthorities);
//                        return new UsernamePasswordAuthenticationToken(
//                                jwt.getClaimAsString(EMAIL_CLAIM),
//                                null,
//                                combinedAuthorities);
//                        //если в запросе нет companyId - возвращаем роли реалма
//                    } else {
//                        log.info("Current user's roles (realm branch): {}", realmAuthorities);
//                        //todo remove
//                        System.out.println(realmAuthorities);
//                        return new UsernamePasswordAuthenticationToken(
//                                jwt.getClaimAsString(EMAIL_CLAIM),
//                                null,
//                                realmAuthorities);
//                    }
//                });
//    }
}