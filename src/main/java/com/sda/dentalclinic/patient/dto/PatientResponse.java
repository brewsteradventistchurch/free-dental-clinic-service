package com.sda.dentalclinic.patient.dto;

import com.sda.dentalclinic.patient.model.Patient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PatientResponse(

        String id,
        String patientNumber,

        String firstName,
        String lastName,

        LocalDate dateOfBirth,

        ContactInformation contact,

        Address streetAddress,
        Address mailingAddress,

        String spouseOrParentName,

        String preferredLanguage,
        boolean needsTranslator,

        RequestInformation request,

        List<String> followUpInterests,
        List<Patient.NoteLog> noteLogs,

        Instant createdAt,
        Instant updatedAt
) {

    public static PatientResponse from(Patient patient) {

        return new PatientResponse(
                patient.getId(),
                patient.getPatientNumber(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getContact() == null
                        ? null
                        : new ContactInformation(
                                patient.getContact().getEmail(),
                                patient.getContact().getCellPhone(),
                                patient.getContact().getAlternatePhone()
                        ),
                patient.getStreetAddress() == null
                        ? null
                        : new Address(
                                patient.getStreetAddress().getAddress(),
                                patient.getStreetAddress().getCity(),
                                patient.getStreetAddress().getState(),
                                patient.getStreetAddress().getZip()
                        ),
                patient.getMailingAddress() == null
                        ? null
                        : new Address(
                                patient.getMailingAddress().getAddress(),
                                patient.getMailingAddress().getCity(),
                                patient.getMailingAddress().getState(),
                                patient.getMailingAddress().getZip()
                        ),
                patient.getSpouseOrParentName(),
                patient.getPreferredLanguage(),
                patient.isNeedsTranslator(),
                patient.getRequest() == null
                        ? null
                        : new RequestInformation(
                                patient.getRequest().getBookingStatus(),
                                patient.getRequest().getSourceOfRequest(),
                                patient.getRequest().getRequestDate(),
                                patient.getRequest().getServiceRequested(),
                                patient.getRequest().getReferral()
                        ),
                patient.getFollowUpInterests(),
                patient.getNoteLogs(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

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