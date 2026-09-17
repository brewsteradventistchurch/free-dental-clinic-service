package com.sda.dentalclinic.scheduling.service;

import com.sda.dentalclinic.scheduling.model.ProviderAvailability;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ProviderAvailabilityService {

    Flux<ProviderAvailability> findAll();

    Flux<ProviderAvailability> findByDate(LocalDate date);

    Mono<ProviderAvailability> findByProviderAndDate(
            String providerId,
            LocalDate date);

    Mono<ProviderAvailability> save(
            ProviderAvailability availability);

    Mono<Void> delete(
            String providerId,
            LocalDate date);

    boolean contains(
            ProviderAvailability availability,
            int startMinutes,
            int endMinutes);

    Flux<ProviderAvailability> findDatesOnOrAfter(
            LocalDate date
    );
}