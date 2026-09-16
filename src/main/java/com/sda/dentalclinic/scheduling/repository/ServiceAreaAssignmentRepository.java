package com.sda.dentalclinic.scheduling.repository;

import com.sda.dentalclinic.scheduling.model.ServiceAreaAssignment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ServiceAreaAssignmentRepository
        extends ReactiveMongoRepository<ServiceAreaAssignment, String> {

    Flux<ServiceAreaAssignment> findByDate(LocalDate date);

    Mono<ServiceAreaAssignment> findByServiceAreaIdAndDate(
            String serviceAreaId,
            LocalDate date);

    Mono<Void> deleteByServiceAreaIdAndDate(
            String serviceAreaId,
            LocalDate date);
}