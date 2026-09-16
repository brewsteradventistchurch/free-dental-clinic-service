package com.sda.dentalclinic.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.*;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;

import java.net.URI;

@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final ClinicOidcUserService clinicOidcUserService;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        var logoutHandler = new DelegatingServerLogoutHandler(
                new SecurityContextServerLogoutHandler(),
                new WebSessionServerLogoutHandler()
        );

        var tokenResponseClient =
                new WebClientReactiveAuthorizationCodeTokenResponseClient();

        var oidcAuthenticationManager =
                new OidcAuthorizationCodeReactiveAuthenticationManager(
                        tokenResponseClient,
                        clinicOidcUserService
                );

        return http
                .csrf(csrfSpec -> csrfSpec
                        .csrfTokenRepository(
                                CookieServerCsrfTokenRepository.withHttpOnlyFalse()
                        ).csrfTokenRequestHandler(
                                new ServerCsrfTokenRequestAttributeHandler()
                        )
                )
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/oauth2/**",
                                "/login/**",
                                "/actuator/health",
                                "/access-pending",
                                "/access-denied"
                        ).permitAll()
                        .pathMatchers("/api/v1/auth/me")
                        .permitAll()
                        .pathMatchers("/api/v1/auth/csrf")
                        .permitAll()

                        /*
                         * Admin endpoints will use this later.
                         */
                        .pathMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        /*
                         * Normal clinic API access.
                         */
                        .pathMatchers("/api/v1/**")
                        .hasAnyRole("VOLUNTEER", "ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationManager(oidcAuthenticationManager)
                        .authenticationSuccessHandler(
                                oauth2LoginSuccessHandler
                        )
                )
                .logout(logout -> logout
                        .logoutHandler(logoutHandler)
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .build();
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {

        RedirectServerLogoutSuccessHandler handler =
                new RedirectServerLogoutSuccessHandler();

        handler.setLogoutSuccessUrl(URI.create("/"));

        return handler;
    }
}