package ru.gnivc.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {
    private final ReactiveJwtDecoder jwtDecoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        BearerTokenAuthenticationToken authenticationToken = (BearerTokenAuthenticationToken) authentication;

        return getJwt(authenticationToken).map(jwt -> {
            String userId = jwt.getClaimAsString("sub");
            String role = jwt.getClaimAsString("roles");
            return new UserDetails()
        })
    }

    private Set<String> getRolesFromJwt(){
    }

    private Mono<Jwt> getJwt(BearerTokenAuthenticationToken bearerToken){
        try{
            return jwtDecoder.decode(bearerToken.getToken());
        } catch (JwtException e){
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }

    public static Set<String> extractRolesFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKeyResolver(publicKeyResolver)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new HashSet<>(claims.get("resource_access", Map.class).get("account").get("roles", List.class));
    }
}
