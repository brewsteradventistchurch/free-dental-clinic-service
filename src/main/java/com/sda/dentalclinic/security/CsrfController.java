package com.sda.dentalclinic.security;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
public class CsrfController {

    @GetMapping("/csrf")
    public Mono<CsrfResponse> csrf(ServerWebExchange exchange) {
        Mono<CsrfToken> csrfToken =
                exchange.getAttribute(CsrfToken.class.getName());

        if (csrfToken == null) {
            return Mono.error(
                    new IllegalStateException("CSRF token is not available")
            );
        }

        return csrfToken.map(token ->
                new CsrfResponse(
                        token.getHeaderName(),
                        token.getToken()
                )
        );
    }

    public record CsrfResponse(
            String headerName,
            String token
    ) {}
}