package com.sda.dentalclinic.user.controller;

import com.sda.dentalclinic.user.model.AdminUserResponse;
import com.sda.dentalclinic.user.model.Role;
import com.sda.dentalclinic.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public Flux<AdminUserResponse> getUsers() {
        return adminUserService.findAll();
    }

    @PutMapping("/{id}/approve")
    public Mono<AdminUserResponse> approve(
            @PathVariable String id,
            Authentication authentication
    ) {
        return adminUserService.approve(
                id,
                googleId(authentication)
        );
    }

    @PutMapping("/{id}/deny")
    public Mono<AdminUserResponse> deny(
            @PathVariable String id,
            Authentication authentication
    ) {
        return adminUserService.deny(
                id,
                googleId(authentication)
        );
    }

    @PutMapping("/{id}/role")
    public Mono<AdminUserResponse> updateRole(
            @PathVariable String id,
            @RequestBody RoleUpdateRequest request,
            Authentication authentication
    ) {
        return adminUserService.updateRole(
                id,
                request.role(),
                googleId(authentication)
        );
    }

    @PutMapping("/{id}/active")
    public Mono<AdminUserResponse> updateActive(
            @PathVariable String id,
            @RequestBody ActiveUpdateRequest request,
            Authentication authentication
    ) {
        return adminUserService.updateActive(
                id,
                request.active(),
                googleId(authentication)
        );
    }

    private String googleId(Authentication authentication) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof OidcUser oidcUser)) {

            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Authenticated administrator could not be identified."
            );
        }

        return oidcUser.getSubject();
    }

    public record RoleUpdateRequest(Role role) {
    }

    public record ActiveUpdateRequest(boolean active) {
    }
}