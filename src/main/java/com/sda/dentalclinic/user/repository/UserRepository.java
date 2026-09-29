package com.sda.dentalclinic.user.repository;

import com.sda.dentalclinic.user.model.Role;
import com.sda.dentalclinic.user.model.Status;
import com.sda.dentalclinic.user.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByGoogleId(String googleId);

    Mono<User> findByEmail(String email);

    Mono<Long> countByRoleAndStatusAndActiveTrue(
            Role role,
            Status status
    );
}