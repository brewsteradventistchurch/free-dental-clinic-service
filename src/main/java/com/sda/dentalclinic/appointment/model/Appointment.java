package com.sda.dentalclinic.appointment.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("appointments")
@CompoundIndex(
        name = "service_area_schedule",
        def = "{'serviceAreaId': 1, 'date': 1, 'startMinutes': 1}"
)
@CompoundIndex(
        name = "provider_schedule",
        def = "{'providerId': 1, 'date': 1, 'startMinutes': 1}"
)
public class Appointment {

    @Id
    private String id;

    private String patientId;

    /**
     * Physical location where the appointment takes place.
     */
    private String serviceAreaId;

    /**
     * Provider assigned to the appointment.
     *
     * This is intentionally stored even though the provider is determined
     * from the service-area assignment when the appointment is created.
     *
     * It preserves the historical provider if future configuration changes.
     */
    private String providerId;

    private String serviceId;

    private LocalDate date;

    /**
     * Minutes since midnight.
     *
     * Example:
     * 08:00 = 480
     */
    private int startMinutes;

    /**
     * Duration of this specific appointment in minutes.
     */
    private int durationMinutes;

    /**
     * Current configurable booking status.
     */
    private String bookingStatus;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}