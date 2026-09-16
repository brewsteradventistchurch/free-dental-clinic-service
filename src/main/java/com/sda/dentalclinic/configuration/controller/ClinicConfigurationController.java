package com.sda.dentalclinic.configuration.controller;

import com.sda.dentalclinic.configuration.dto.ClinicConfigurationResponse;
import com.sda.dentalclinic.configuration.model.ClinicConfiguration;
import com.sda.dentalclinic.configuration.service.ClinicConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configuration")
@RequiredArgsConstructor
public class ClinicConfigurationController {

    private final ClinicConfigurationService configurationService;

    @GetMapping
    public Mono<ClinicConfigurationResponse> getConfiguration() {
        return configurationService.getConfiguration()
                .map(ClinicConfigurationResponse::from);
    }

    @PutMapping("/services")
    public Mono<ClinicConfigurationResponse> updateServices(
            @RequestBody List<ClinicConfiguration.Service> services) {

        return configurationService.updateServices(services)
                .map(ClinicConfigurationResponse::from);
    }

    @PutMapping("/providers")
    public Mono<ClinicConfigurationResponse> updateProviders(
            @RequestBody List<ClinicConfiguration.Provider> providers) {

        return configurationService.updateProviders(providers)
                .map(ClinicConfigurationResponse::from);
    }

    @PutMapping("/service-areas")
    public Mono<ClinicConfigurationResponse> updateServiceAreas(
            @RequestBody List<ClinicConfiguration.ServiceArea> serviceAreas) {

        return configurationService.updateServiceAreas(serviceAreas)
                .map(ClinicConfigurationResponse::from);
    }

    @PutMapping("/booking-statuses")
    public Mono<ClinicConfigurationResponse> updateBookingStatuses(
            @RequestBody List<ClinicConfiguration.BookingStatus> bookingStatuses) {

        return configurationService.updateBookingStatuses(bookingStatuses)
                .map(ClinicConfigurationResponse::from);
    }

    @PutMapping("/request-sources")
    public Mono<ClinicConfigurationResponse> updateRequestSources(
            @RequestBody List<String> requestSources) {

        return configurationService.updateRequestSources(requestSources)
                .map(ClinicConfigurationResponse::from);
    }

    @PutMapping("/preferred-languages")
    public Mono<ClinicConfigurationResponse> updatePreferredLanguages(
            @RequestBody List<String> preferredLanguages) {

        return configurationService.updatePreferredLanguages(preferredLanguages)
                .map(ClinicConfigurationResponse::from);
    }

    @PutMapping("/community-follow-ups")
    public Mono<ClinicConfigurationResponse> updateCommunityFollowUps(
            @RequestBody List<String> communityFollowUps) {

        return configurationService.updateCommunityFollowUps(communityFollowUps)
                .map(ClinicConfigurationResponse::from);
    }
}