package com.sda.dentalclinic.security;

import com.sda.dentalclinic.user.model.Role;
import com.sda.dentalclinic.user.model.Status;
import com.sda.dentalclinic.user.model.User;
import com.sda.dentalclinic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    public Mono<ResponseEntity<CurrentUserResponse>> currentUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof OidcUser oidcUser)) {

            return Mono.just(ResponseEntity.status(401).build());
        }

        String googleId = oidcUser.getSubject();

        return userService.findByGoogleId(googleId)
                .filter(User::isActive)
                .filter(user -> user.getStatus() == Status.APPROVED)
                .map(user -> ResponseEntity.ok(
                        CurrentUserResponse.from(user)
                ))
                .defaultIfEmpty(ResponseEntity.status(403).build());
    }

    public record CurrentUserResponse(
            String id,
            String email,
            String firstName,
            String lastName,
            Role role
    ) {

        public static CurrentUserResponse from(User user) {
            return new CurrentUserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole()
            );
        }
    }
}