package ru.gnivc.gatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

@Slf4j
public class CustomGrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
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
