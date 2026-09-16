package com.sda.dentalclinic.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        implements ServerAuthenticationSuccessHandler {

    private static final Logger log =
            LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public Mono<Void> onAuthenticationSuccess(
            WebFilterExchange webFilterExchange,
            Authentication authentication) {

        log.info(
                "Google OAuth authentication succeeded for principal: {}",
                authentication.getName()
        );

        String destination;

        if (hasAuthority(authentication, "CLINIC_PENDING")) {

            log.info(
                    "User is pending clinic approval: {}",
                    authentication.getName()
            );

            destination = frontendUrl + "/access-pending";

        } else if (hasAuthority(authentication, "CLINIC_DENIED")) {

            log.info(
                    "User is denied or inactive: {}",
                    authentication.getName()
            );

            destination = frontendUrl + "/access-denied";

        } else if (hasAuthority(authentication, "ROLE_ADMIN")
                || hasAuthority(authentication, "ROLE_VOLUNTEER")) {

            log.info(
                    "Approved clinic user successfully authenticated: {}",
                    authentication.getName()
            );

            destination = frontendUrl + "/dashboard";

        } else {

            log.warn(
                    "Authenticated Google user has no recognized clinic authority: {}",
                    authentication.getName()
            );

            destination = frontendUrl + "/access-denied";
        }

        var response = webFilterExchange.getExchange().getResponse();

        response.setStatusCode(
                org.springframework.http.HttpStatus.FOUND
        );

        response.getHeaders().setLocation(
                URI.create(destination)
        );

        return response.setComplete();
    }

    private boolean hasAuthority(
            Authentication authentication,
            String authority) {

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }
}