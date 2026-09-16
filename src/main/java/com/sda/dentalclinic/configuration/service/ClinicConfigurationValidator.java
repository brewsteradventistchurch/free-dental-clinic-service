package com.sda.dentalclinic.configuration.service;

import com.sda.dentalclinic.configuration.model.ClinicConfiguration;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ClinicConfigurationValidator {

    public void validate(ClinicConfiguration configuration) {

        validateServices(configuration.getServices());
        validateProviders(
                configuration.getProviders(),
                configuration.getServices()
        );
        validateServiceAreas(configuration.getServiceAreas());
        validateBookingStatuses(configuration.getBookingStatuses());
    }

    private void validateServices(
            List<ClinicConfiguration.Service> services) {

        if (services == null) {
            return;
        }

        Set<String> ids = new HashSet<>();

        for (ClinicConfiguration.Service service : services) {

            requireId(service.getId(), "Service");

            requireName(
                    service.getName(),
                    "Service",
                    service.getId()
            );

            if (!ids.add(service.getId())) {
                throw new IllegalArgumentException(
                        "Duplicate service ID: " + service.getId()
                );
            }

            if (service.getDefaultDurationMinutes() <= 0) {
                throw new IllegalArgumentException(
                        "Service duration must be greater than zero: "
                                + service.getId()
                );
            }

            if (service.getDefaultDurationMinutes() > 24 * 60) {
                throw new IllegalArgumentException(
                        "Service duration cannot exceed 24 hours: "
                                + service.getId()
                );
            }
        }
    }

    private void validateProviders(
            List<ClinicConfiguration.Provider> providers,
            List<ClinicConfiguration.Service> services) {

        if (providers == null) {
            return;
        }

        Set<String> providerIds = new HashSet<>();

        Set<String> serviceIds = new HashSet<>();

        if (services != null) {
            for (ClinicConfiguration.Service service : services) {
                if (service.getId() != null) {
                    serviceIds.add(service.getId());
                }
            }
        }

        for (ClinicConfiguration.Provider provider : providers) {
            requireId(provider.getId(), "Provider");
            requireName(provider.getName(), "Provider", provider.getId());

            if (!providerIds.add(provider.getId())) {
                throw new IllegalArgumentException(
                        "Duplicate provider ID: " + provider.getId()
                );
            }

            if (provider.getServiceIds() != null) {
                Set<String> providerServiceIds = new HashSet<>();

                for (String serviceId : provider.getServiceIds()) {
                    if (serviceId == null || serviceId.isBlank()) {
                        throw new IllegalArgumentException(
                                "Provider contains a blank service ID: "
                                        + provider.getId()
                        );
                    }

                    if (!providerServiceIds.add(serviceId)) {
                        throw new IllegalArgumentException(
                                "Provider " + provider.getId()
                                        + " contains duplicate service ID: "
                                        + serviceId
                        );
                    }

                    if (!serviceIds.contains(serviceId)) {
                        throw new IllegalArgumentException(
                                "Provider " + provider.getId()
                                        + " references unknown service ID: "
                                        + serviceId
                        );
                    }
                }
            }
        }
    }

    private void validateServiceAreas(
            List<ClinicConfiguration.ServiceArea> serviceAreas) {

        if (serviceAreas == null) {
            return;
        }

        Set<String> ids = new HashSet<>();

        for (ClinicConfiguration.ServiceArea serviceArea
                : serviceAreas) {

            requireId(serviceArea.getId(), "Service area");

            requireName(
                    serviceArea.getName(),
                    "Service area",
                    serviceArea.getId()
            );

            if (!ids.add(serviceArea.getId())) {
                throw new IllegalArgumentException(
                        "Duplicate service area ID: "
                                + serviceArea.getId()
                );
            }
        }
    }

    private void validateBookingStatuses(
            List<ClinicConfiguration.BookingStatus> bookingStatuses) {

        if (bookingStatuses == null) {
            return;
        }

        Set<String> ids = new HashSet<>();

        for (ClinicConfiguration.BookingStatus bookingStatus
                : bookingStatuses) {

            requireId(
                    bookingStatus.getId(),
                    "Booking status"
            );

            requireName(
                    bookingStatus.getName(),
                    "Booking status",
                    bookingStatus.getId()
            );

            if (!ids.add(bookingStatus.getId())) {
                throw new IllegalArgumentException(
                        "Duplicate booking status ID: "
                                + bookingStatus.getId()
                );
            }
        }
    }

    private void requireId(
            String id,
            String type) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    type + " ID is required."
            );
        }
    }

    private void requireName(
            String name,
            String type,
            String id) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    type + " name is required: " + id
            );
        }
    }
}