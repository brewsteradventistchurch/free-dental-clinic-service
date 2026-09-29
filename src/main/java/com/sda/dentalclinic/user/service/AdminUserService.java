package com.sda.dentalclinic.user.service;

import com.sda.dentalclinic.user.model.AdminUserResponse;
import com.sda.dentalclinic.user.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminUserService {

    Flux<AdminUserResponse> findAll();

    Mono<AdminUserResponse> approve(
            String userId,
            String actorGoogleId
    );

    Mono<AdminUserResponse> deny(
            String userId,
            String actorGoogleId
    );

    Mono<AdminUserResponse> updateRole(
            String userId,
            Role role,
            String actorGoogleId
    );

    Mono<AdminUserResponse> updateActive(
            String userId,
            boolean active,
            String actorGoogleId
    );
}