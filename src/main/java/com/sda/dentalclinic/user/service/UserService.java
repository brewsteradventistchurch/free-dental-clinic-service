package com.sda.dentalclinic.user.service;

import com.sda.dentalclinic.user.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> findByGoogleId(String googleId);

    Mono<User> findByEmail(String email);

    Flux<User> findAll();

    Mono<User> save(User user);
}