package com.sda.dentalclinic.user.service;

import com.sda.dentalclinic.user.model.AdminUserResponse;
import com.sda.dentalclinic.user.model.Role;
import com.sda.dentalclinic.user.model.Status;
import com.sda.dentalclinic.user.model.User;
import com.sda.dentalclinic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    public Flux<AdminUserResponse> findAll() {
        return userRepository.findAll()
                .map(this::toResponse);
    }

    @Override
    public Mono<AdminUserResponse> approve(
            String userId,
            String actorGoogleId
    ) {
        return loadTargetForModification(
                userId,
                actorGoogleId
        ).flatMap(user -> {
            user.setStatus(Status.APPROVED);
            user.setActive(true);
            user.setApprovedAt(Instant.now());

            return userRepository.save(user);
        }).map(this::toResponse);
    }

    @Override
    public Mono<AdminUserResponse> deny(
            String userId,
            String actorGoogleId
    ) {
        return loadTargetForModification(
                userId,
                actorGoogleId
        ).flatMap(user -> {
            if (user.getStatus() != Status.PENDING) {
                return Mono.error(
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Only pending users can be denied."
                        )
                );
            }

            user.setStatus(Status.DENIED);
            user.setActive(false);

            return userRepository.save(user);
        }).map(this::toResponse);
    }

    @Override
    public Mono<AdminUserResponse> updateRole(
            String userId,
            Role role,
            String actorGoogleId
    ) {
        return loadTargetForModification(
                userId,
                actorGoogleId
        ).flatMap(user -> {

            if (user.getStatus() != Status.APPROVED) {
                return Mono.error(
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Only approved users can have their role changed."
                        )
                );
            }

            Role previousRole = user.getRole();

            if (previousRole == Role.ADMIN
                    && role != Role.ADMIN
                    && user.isActive()) {

                return ensureAnotherActiveAdmin()
                        .thenReturn(user);
            }

            user.setRole(role);

            return userRepository.save(user);
        }).map(this::toResponse);
    }

    @Override
    public Mono<AdminUserResponse> updateActive(
            String userId,
            boolean active,
            String actorGoogleId
    ) {
        return loadTargetForModification(
                userId,
                actorGoogleId
        ).flatMap(user -> {

            if (user.getStatus() != Status.APPROVED) {
                return Mono.error(
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Only approved users can be enabled or disabled."
                        )
                );
            }

            if (!active
                    && user.getRole() == Role.ADMIN
                    && user.isActive()) {

                return ensureAnotherActiveAdmin()
                        .thenReturn(user)
                        .flatMap(ignored -> {
                            user.setActive(false);
                            return userRepository.save(user);
                        });
            }

            user.setActive(active);

            return userRepository.save(user);
        }).map(this::toResponse);
    }

    private Mono<User> loadTargetForModification(
            String userId,
            String actorGoogleId
    ) {
        return userRepository.findById(userId)
                .switchIfEmpty(
                        Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found."
                                )
                        )
                )
                .flatMap(target ->
                        userRepository.findByGoogleId(actorGoogleId)
                                .switchIfEmpty(
                                        Mono.error(
                                                new ResponseStatusException(
                                                        HttpStatus.UNAUTHORIZED,
                                                        "The current administrator could not be identified."
                                                )
                                        )
                                )
                                .flatMap(actor -> {

                                    if (actor.getId() != null
                                            && actor.getId().equals(target.getId())) {

                                        return Mono.error(
                                                new ResponseStatusException(
                                                        HttpStatus.BAD_REQUEST,
                                                        "Administrators cannot change their own access."
                                                )
                                        );
                                    }

                                    return Mono.just(target);
                                })
                );
    }

    private Mono<Void> ensureAnotherActiveAdmin() {
        return userRepository.countByRoleAndStatusAndActiveTrue(
                        Role.ADMIN,
                        Status.APPROVED
                )
                .flatMap(count -> {
                    if (count <= 1) {
                        return Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "The clinic must always have at least one active administrator."
                                )
                        );
                    }

                    return Mono.empty();
                });
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getStatus(),
                user.isActive(),
                user.getCreatedDate(),
                user.getApprovedAt(),
                user.getLastLogin()
        );
    }
}