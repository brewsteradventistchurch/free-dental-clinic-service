package com.sda.dentalclinic.configuration.dto;

import com.sda.dentalclinic.configuration.model.ClinicConfiguration;

import java.time.Instant;
import java.util.List;

public record ClinicConfigurationResponse(
        String id,
        List<ClinicConfiguration.Service> services,
        List<ClinicConfiguration.Provider> providers,
        List<ClinicConfiguration.ServiceArea> serviceAreas,
        List<ClinicConfiguration.BookingStatus> bookingStatuses,
        List<String> requestSources,
        List<String> preferredLanguages,
        List<String> communityFollowUps,
        Instant updatedAt
) {

    public static ClinicConfigurationResponse from(
            ClinicConfiguration configuration) {

        return new ClinicConfigurationResponse(
                configuration.getId(),
                configuration.getServices(),
                configuration.getProviders(),
                configuration.getServiceAreas(),
                configuration.getBookingStatuses(),
                configuration.getRequestSources(),
                configuration.getPreferredLanguages(),
                configuration.getCommunityFollowUps(),
                configuration.getUpdatedAt()
        );
    }
}