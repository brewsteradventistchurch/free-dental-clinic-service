package com.sda.dentalclinic.scheduling.service;

import com.sda.dentalclinic.scheduling.model.ServiceAreaAssignment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ServiceAreaAssignmentService {

    Flux<ServiceAreaAssignment> findByDate(LocalDate date);

    Mono<ServiceAreaAssignment> findByServiceAreaAndDate(
            String serviceAreaId,
            LocalDate date);

    Mono<ServiceAreaAssignment> assign(
            String serviceAreaId,
            String providerId,
            LocalDate date);

    Mono<Void> delete(
            String serviceAreaId,
            LocalDate date);
}