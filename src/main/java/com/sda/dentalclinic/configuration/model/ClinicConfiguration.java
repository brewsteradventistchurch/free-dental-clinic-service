package com.sda.dentalclinic.configuration.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("clinic_configuration")
public class ClinicConfiguration {

    /**
     * There is currently one clinic configuration document.
     * Using a fixed ID makes this explicit and prevents multiple
     * configuration documents from accidentally being created.
     */
    @Id
    @Builder.Default
    private String id = "clinic";

    @Version
    private Long version;

    @Builder.Default
    private List<Service> services = new ArrayList<>();

    @Builder.Default
    private List<Provider> providers = new ArrayList<>();

    @Builder.Default
    private List<ServiceArea> serviceAreas = new ArrayList<>();

    @Builder.Default
    private List<BookingStatus> bookingStatuses = new ArrayList<>();

    @Builder.Default
    private List<String> requestSources = new ArrayList<>();

    @Builder.Default
    private List<String> preferredLanguages = new ArrayList<>();

    @Builder.Default
    private List<String> communityFollowUps = new ArrayList<>();

    @LastModifiedDate
    private Instant updatedAt;


    // -------------------------------------------------------------------------
    // Services
    // -------------------------------------------------------------------------

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Service {

        private String id;

        private String name;

        /**
         * Default appointment duration in minutes.
         * The scheduler may eventually allow an administrator
         * to override this per appointment when appropriate.
         */
        private int defaultDurationMinutes;

        private boolean active;

        /**
         * Controls the order in which services are displayed.
         */
        private int displayOrder;
    }


    // -------------------------------------------------------------------------
    // Providers
    // -------------------------------------------------------------------------

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Provider {

        private String id;

        private String name;

        /**
         * Services this provider is qualified/configured to perform.
         */
        @Builder.Default
        private List<String> serviceIds = new ArrayList<>();

        private boolean active;

        private int displayOrder;
    }


    // -------------------------------------------------------------------------
    // Service Areas
    // -------------------------------------------------------------------------

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceArea {

        private String id;

        private String name;

        private boolean active;

        private int displayOrder;
    }


    // -------------------------------------------------------------------------
    // Booking Statuses
    // -------------------------------------------------------------------------

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingStatus {

        private String id;

        private String name;

        private boolean active;

        private int displayOrder;
    }
}