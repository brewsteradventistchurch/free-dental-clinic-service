package com.sda.dentalclinic.appointment.dto;

import com.sda.dentalclinic.appointment.model.Appointment;

import java.time.Instant;
import java.time.LocalDate;

public record AppointmentResponse(
        String id,
        String patientId,
        String serviceAreaId,
        String providerId,
        String serviceId,
        LocalDate date,
        int startMinutes,
        int durationMinutes,
        String bookingStatus,
        Instant createdAt,
        Instant updatedAt
) {

    public static AppointmentResponse from(
            Appointment appointment) {

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getServiceAreaId(),
                appointment.getProviderId(),
                appointment.getServiceId(),
                appointment.getDate(),
                appointment.getStartMinutes(),
                appointment.getDurationMinutes(),
                appointment.getBookingStatus(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}