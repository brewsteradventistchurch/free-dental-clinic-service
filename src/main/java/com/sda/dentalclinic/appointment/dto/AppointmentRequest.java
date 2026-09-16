package com.sda.dentalclinic.appointment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AppointmentRequest(

        @NotBlank
        String patientId,

        @NotBlank
        String serviceAreaId,

        @NotBlank
        String serviceId,

        @NotNull
        LocalDate date,

        @Min(0)
        int startMinutes,

        @Min(1)
        int durationMinutes,

        @NotBlank
        String bookingStatus
) {
}