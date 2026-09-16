package com.sda.dentalclinic.security;

import com.sda.dentalclinic.user.model.Role;
import com.sda.dentalclinic.user.model.Status;
import com.sda.dentalclinic.user.model.User;
import com.sda.dentalclinic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClinicOidcUserService
        implements org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService<
        OidcUserRequest,
        OidcUser> {

    private final UserService userService;

    private final OidcReactiveOAuth2UserService delegate =
            new OidcReactiveOAuth2UserService();

    @Override
    public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {

        return delegate.loadUser(userRequest)
                .flatMap(this::synchronizeUser);
    }

    private Mono<OidcUser> synchronizeUser(OidcUser oidcUser) {

        String googleId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String firstName = oidcUser.getGivenName();
        String lastName = oidcUser.getFamilyName();

        if (googleId == null || email == null) {
            return Mono.error(new IllegalStateException(
                    "Google authentication did not provide the required user information"
            ));
        }

        return userService.findByGoogleId(googleId)
                .flatMap(existingUser -> updateExistingUser(
                        existingUser,
                        oidcUser
                ))
                .switchIfEmpty(
                        createPendingUser(
                                googleId,
                                email,
                                firstName,
                                lastName
                        )
                )
                .map(user -> createAuthenticatedPrincipal(
                        oidcUser,
                        user
                ));
    }

    private Mono<User> updateExistingUser(
            User user,
            OidcUser oidcUser) {

        user.setLastLogin(Instant.now());

        return userService.save(user);
    }

    private Mono<User> createPendingUser(
            String googleId,
            String email,
            String firstName,
            String lastName) {

        User user = User.builder()
                .googleId(googleId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(Role.VOLUNTEER)
                .status(Status.PENDING)
                .active(true)
                .createdDate(Instant.now())
                .lastLogin(Instant.now())
                .build();

        return userService.save(user);
    }

    private OidcUser createAuthenticatedPrincipal(
            OidcUser oidcUser,
            User user) {

        Set<GrantedAuthority> authorities =
                new HashSet<>(oidcUser.getAuthorities());

        /*
         * Only approved and active users receive an application role.
         */
        if (user.isActive() && user.getStatus() == Status.APPROVED) {

            if (user.getRole() == Role.ADMIN) {
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                );
            }

            if (user.getRole() == Role.VOLUNTEER) {
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_VOLUNTEER")
                );
            }
        }

        /*
         * These authorities are used by the login success handler
         * to distinguish pending and denied users.
         */
        if (user.getStatus() == Status.PENDING) {
            authorities.add(
                    new SimpleGrantedAuthority("CLINIC_PENDING")
            );
        }

        if (user.getStatus() == Status.DENIED || !user.isActive()) {
            authorities.add(
                    new SimpleGrantedAuthority("CLINIC_DENIED")
            );
        }

        OidcUserInfo userInfo = oidcUser.getUserInfo();

        if (userInfo != null) {
            return new DefaultOidcUser(
                    authorities,
                    oidcUser.getIdToken(),
                    userInfo
            );
        }

        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken()
        );
    }
}