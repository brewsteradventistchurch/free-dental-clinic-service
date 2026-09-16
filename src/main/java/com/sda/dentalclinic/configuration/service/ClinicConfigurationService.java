package com.sda.dentalclinic.configuration.service;

import com.sda.dentalclinic.configuration.model.ClinicConfiguration;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ClinicConfigurationService {

    Mono<ClinicConfiguration> getConfiguration();

    Mono<ClinicConfiguration> updateServices(
            List<ClinicConfiguration.Service> services);

    Mono<ClinicConfiguration> updateProviders(
            List<ClinicConfiguration.Provider> providers);

    Mono<ClinicConfiguration> updateServiceAreas(
            List<ClinicConfiguration.ServiceArea> serviceAreas);

    Mono<ClinicConfiguration> updateBookingStatuses(
            List<ClinicConfiguration.BookingStatus> bookingStatuses);

    Mono<ClinicConfiguration> updateRequestSources(
            List<String> requestSources);

    Mono<ClinicConfiguration> updatePreferredLanguages(
            List<String> preferredLanguages);

    Mono<ClinicConfiguration> updateCommunityFollowUps(
            List<String> communityFollowUps);
}