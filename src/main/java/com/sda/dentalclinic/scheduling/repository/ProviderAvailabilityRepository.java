package com.sda.dentalclinic.scheduling.repository;

import com.sda.dentalclinic.scheduling.model.ProviderAvailability;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ProviderAvailabilityRepository
        extends ReactiveMongoRepository<ProviderAvailability, String> {

    Flux<ProviderAvailability> findByDate(LocalDate date);

    Mono<ProviderAvailability> findByProviderIdAndDate(
            String providerId,
            LocalDate date);

    Mono<Void> deleteByProviderIdAndDate(
            String providerId,
            LocalDate date);
}