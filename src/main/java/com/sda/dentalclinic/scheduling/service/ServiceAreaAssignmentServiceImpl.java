package com.sda.dentalclinic.scheduling.service;

import com.sda.dentalclinic.configuration.model.ClinicConfiguration;
import com.sda.dentalclinic.configuration.service.ClinicConfigurationService;
import com.sda.dentalclinic.scheduling.model.ServiceAreaAssignment;
import com.sda.dentalclinic.scheduling.repository.ServiceAreaAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ServiceAreaAssignmentServiceImpl
        implements ServiceAreaAssignmentService {

    private final ServiceAreaAssignmentRepository repository;
    private final ClinicConfigurationService configurationService;

    @Override
    public Flux<ServiceAreaAssignment> findByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    @Override
    public Mono<ServiceAreaAssignment> findByServiceAreaAndDate(
            String serviceAreaId,
            LocalDate date) {

        return repository.findByServiceAreaIdAndDate(
                serviceAreaId,
                date
        );
    }

    @Override
    public Mono<ServiceAreaAssignment> assign(
            String serviceAreaId,
            String providerId,
            LocalDate date) {

        return configurationService.getConfiguration()
                .flatMap(configuration -> {

                    ClinicConfiguration.ServiceArea serviceArea =
                            configuration.getServiceAreas()
                                    .stream()
                                    .filter(area ->
                                            area.getId().equals(serviceAreaId)
                                    )
                                    .findFirst()
                                    .orElse(null);

                    if (serviceArea == null) {
                        return Mono.error(
                                new IllegalArgumentException(
                                        "Service area does not exist: "
                                                + serviceAreaId
                                )
                        );
                    }

                    if (!serviceArea.isActive()) {
                        return Mono.error(
                                new IllegalStateException(
                                        "Service area is inactive: "
                                                + serviceAreaId
                                )
                        );
                    }

                    ClinicConfiguration.Provider provider =
                            configuration.getProviders()
                                    .stream()
                                    .filter(item ->
                                            item.getId().equals(providerId)
                                    )
                                    .findFirst()
                                    .orElse(null);

                    if (provider == null) {
                        return Mono.error(
                                new IllegalArgumentException(
                                        "Provider does not exist: "
                                                + providerId
                                )
                        );
                    }

                    if (!provider.isActive()) {
                        return Mono.error(
                                new IllegalStateException(
                                        "Provider is inactive: "
                                                + providerId
                                )
                        );
                    }

                    return repository
                            .findByServiceAreaIdAndDate(
                                    serviceAreaId,
                                    date
                            )
                            .defaultIfEmpty(
                                    ServiceAreaAssignment.builder()
                                            .serviceAreaId(serviceAreaId)
                                            .date(date)
                                            .build()
                            )
                            .flatMap(existing -> {

                                existing.setProviderId(providerId);
                                existing.setDate(date);
                                existing.setServiceAreaId(serviceAreaId);

                                return repository.save(existing);
                            });
                });
    }

    @Override
    public Mono<Void> delete(
            String serviceAreaId,
            LocalDate date) {

        return repository.deleteByServiceAreaIdAndDate(
                serviceAreaId,
                date
        );
    }
}