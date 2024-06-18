package ru.gnivc.gatewayservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.gnivc.gatewayservice.filter.RequestFilter;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class CustomGrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final RequestFilter requestFilter;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> realmRoles = getRealmRoles(jwt);
        Set<GrantedAuthority> realmAuthorities = realmRoles.stream()
                .filter(role -> role.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        log.info("authorities: {}", realmAuthorities);

        if (requestFilter.getCompanyNameFromRequest().isEmpty()) {
            return realmAuthorities;
        } else {
            String companyName = requestFilter.getCompanyNameFromRequest().get();
            List<String> clientRoles = getClientRoles(jwt, companyName);
            Set<GrantedAuthority> clientAuthorities = clientRoles.stream()
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            Set<GrantedAuthority> combinedAuthorities = Stream
                    .concat(realmAuthorities.stream(), clientAuthorities.stream())
                    .collect(Collectors.toSet());
            log.info("Current user's roles (client branch): {}", combinedAuthorities);
            return combinedAuthorities;
        }
    }

    private static List<String> getRealmRoles(Jwt jwt) {
        List<String> realmRoles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
        return realmRoles;
    }


    private static List<String> getClientRoles(Jwt jwt, String name) {
        Map<String, Object> resourceAccessClaims = jwt.getClaimAsMap("resource_access");
        Map<String, Object> clientClaims = (Map<String, Object>) resourceAccessClaims.get(name);
        if (clientClaims == null) {
            return Collections.emptyList();
        } else {
            List<String> clientRoles = (List<String>) clientClaims.get("roles");
            return clientRoles;
        }
    }
}
