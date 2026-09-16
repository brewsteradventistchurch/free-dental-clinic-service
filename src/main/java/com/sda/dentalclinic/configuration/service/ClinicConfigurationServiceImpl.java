package com.sda.dentalclinic.configuration.service;

import com.sda.dentalclinic.configuration.model.ClinicConfiguration;
import com.sda.dentalclinic.configuration.repository.ClinicConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ClinicConfigurationServiceImpl
        implements ClinicConfigurationService {

    private static final String CONFIGURATION_ID = "clinic";

    private final ClinicConfigurationRepository repository;
    private final ClinicConfigurationValidator validator;

    @Override
    public Mono<ClinicConfiguration> getConfiguration() {
        return getOrCreateConfiguration();
    }

    @Override
    public Mono<ClinicConfiguration> updateServices(
            List<ClinicConfiguration.Service> services) {

        return update(configuration ->
                configuration.setServices(copyList(services)));
    }

    @Override
    public Mono<ClinicConfiguration> updateProviders(
            List<ClinicConfiguration.Provider> providers) {

        return update(configuration ->
                configuration.setProviders(copyList(providers)));
    }

    @Override
    public Mono<ClinicConfiguration> updateServiceAreas(
            List<ClinicConfiguration.ServiceArea> serviceAreas) {

        return update(configuration ->
                configuration.setServiceAreas(copyList(serviceAreas)));
    }

    @Override
    public Mono<ClinicConfiguration> updateBookingStatuses(
            List<ClinicConfiguration.BookingStatus> bookingStatuses) {

        return update(configuration ->
                configuration.setBookingStatuses(copyList(bookingStatuses)));
    }

    @Override
    public Mono<ClinicConfiguration> updateRequestSources(
            List<String> requestSources) {

        return update(configuration ->
                configuration.setRequestSources(copyList(requestSources)));
    }

    @Override
    public Mono<ClinicConfiguration> updatePreferredLanguages(
            List<String> preferredLanguages) {

        return update(configuration ->
                configuration.setPreferredLanguages(copyList(preferredLanguages)));
    }

    @Override
    public Mono<ClinicConfiguration> updateCommunityFollowUps(
            List<String> communityFollowUps) {

        return update(configuration ->
                configuration.setCommunityFollowUps(copyList(communityFollowUps)));
    }

    private Mono<ClinicConfiguration> getOrCreateConfiguration() {
        return repository.findById(CONFIGURATION_ID)
                .switchIfEmpty(
                        repository.save(
                                ClinicConfiguration.builder()
                                        .id(CONFIGURATION_ID)
                                        .build()
                        )
                );
    }

    private Mono<ClinicConfiguration> update(
            Consumer<ClinicConfiguration> change) {

        return getOrCreateConfiguration()
                .flatMap(configuration -> {
                    change.accept(configuration);

                    validator.validate(configuration);

                    return repository.save(configuration);
                });
    }

    private <T> List<T> copyList(List<T> values) {
        return values == null
                ? new ArrayList<>()
                : new ArrayList<>(values);
    }
}