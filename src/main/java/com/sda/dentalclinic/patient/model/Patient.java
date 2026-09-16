package com.sda.dentalclinic.patient.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("patients")
public class Patient {

    @Id
    private String id;

    /**
     * Human-facing clinic identifier.
     * This is separate from MongoDB's technical document ID.
     */
    @Indexed(unique = true)
    private String patientNumber;

    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;

    private ContactInformation contact;

    private Address streetAddress;
    private Address mailingAddress;

    private String spouseOrParentName;

    private String preferredLanguage;
    private boolean needsTranslator;

    private RequestInformation request;

    /**
     * IDs of configured community follow-up interests.
     */
    @Builder.Default
    private List<String> followUpInterests = new ArrayList<>();

    /**
     * Patient notes are embedded because they belong exclusively
     * to this patient and are normally read with the patient record.
     */
    @Builder.Default
    private List<NoteLog> noteLogs = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInformation {

        private String email;
        private String cellPhone;
        private String alternatePhone;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {

        private String address;
        private String city;
        private String state;
        private String zip;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInformation {

        private String bookingStatus;
        private String sourceOfRequest;
        private Instant requestDate;

        /**
         * ID of the requested service from clinic configuration.
         */
        private String serviceRequested;

        /**
         * Optional referral information supplied during registration.
         */
        private String referral;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteLog {

        private String id;
        private String note;
        private Instant createdAt;
        private String volunteerId;
        private String volunteerName;
    }
}