package com.sda.dentalclinic.patient.dto;

import com.sda.dentalclinic.patient.model.Patient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PatientRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        LocalDate dateOfBirth,

        @Valid
        ContactInformation contact,

        @Valid
        Address streetAddress,

        @Valid
        Address mailingAddress,

        String spouseOrParentName,

        String preferredLanguage,

        boolean needsTranslator,

        @Valid
        RequestInformation request,

        List<String> followUpInterests,

        List<Patient.NoteLog> noteLogs
) {

    public record ContactInformation(
            String email,
            String cellPhone,
            String alternatePhone
    ) {
    }

    public record Address(
            String address,
            String city,
            String state,
            String zip
    ) {
    }

    public record RequestInformation(
            String bookingStatus,
            String sourceOfRequest,
            Instant requestDate,
            String serviceRequested,
            String referral
    ) {
    }
}