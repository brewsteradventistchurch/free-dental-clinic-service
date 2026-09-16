package com.sda.dentalclinic.patient.service;

import com.sda.dentalclinic.counter.service.CounterService;
import com.sda.dentalclinic.patient.dto.PatientRequest;
import com.sda.dentalclinic.patient.model.Patient;
import com.sda.dentalclinic.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private static final String PATIENT_NUMBER_COUNTER =
            "patientNumber";

    private final PatientRepository patientRepository;
    private final CounterService counterService;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Patient> findById(String id) {
        return patientRepository.findById(id);
    }

    @Override
    public Mono<Patient> findByPatientNumber(String patientNumber) {
        return patientRepository.findByPatientNumber(patientNumber);
    }

    @Override
    public Flux<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    public Flux<Patient> search(String query) {

        String normalized = query == null
                ? ""
                : query.trim();

        if (normalized.isEmpty()) {
            return Flux.empty();
        }

        String regex = Pattern.quote(normalized);

        Criteria criteria = new Criteria().orOperator(
                Criteria.where("patientNumber")
                        .regex(regex, "i"),

                Criteria.where("firstName")
                        .regex(regex, "i"),

                Criteria.where("lastName")
                        .regex(regex, "i"),

                Criteria.where("contact.cellPhone")
                        .regex(regex, "i"),

                Criteria.where("contact.alternatePhone")
                        .regex(regex, "i"),

                Criteria.where("contact.email")
                        .regex(regex, "i")
        );

        Query searchQuery = Query.query(criteria);

        return mongoTemplate.find(searchQuery, Patient.class);
    }

    @Override
    public Mono<Patient> create(PatientRequest request) {

        return counterService
                .nextSequence(PATIENT_NUMBER_COUNTER)
                .map(this::formatPatientNumber)
                .flatMap(patientNumber -> {

                    Patient patient = toPatient(
                            request,
                            patientNumber
                    );

                    return patientRepository.save(patient);
                });
    }

    @Override
    public Mono<Patient> update(
            String id,
            PatientRequest request) {

        return patientRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new PatientNotFoundException(id)
                        )
                )
                .flatMap(existingPatient -> {

                    applyRequest(
                            existingPatient,
                            request
                    );

                    return patientRepository.save(
                            existingPatient
                    );
                });
    }

    private String formatPatientNumber(long sequence) {

        return String.format(
                "P-%06d",
                sequence
        );
    }

    private Patient toPatient(
            PatientRequest request,
            String patientNumber) {

        Patient patient = Patient.builder()
                .patientNumber(patientNumber)
                .build();

        applyRequest(patient, request);

        return patient;
    }

    private void applyRequest(
            Patient patient,
            PatientRequest request) {

        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setDateOfBirth(request.dateOfBirth());

        if (request.contact() != null) {

            patient.setContact(
                    Patient.ContactInformation.builder()
                            .email(request.contact().email())
                            .cellPhone(request.contact().cellPhone())
                            .alternatePhone(
                                    request.contact().alternatePhone()
                            )
                            .build()
            );
        } else {
            patient.setContact(null);
        }

        if (request.streetAddress() != null) {

            patient.setStreetAddress(
                    toAddress(request.streetAddress())
            );
        } else {
            patient.setStreetAddress(null);
        }

        if (request.mailingAddress() != null) {

            patient.setMailingAddress(
                    toAddress(request.mailingAddress())
            );
        } else {
            patient.setMailingAddress(null);
        }

        patient.setSpouseOrParentName(
                request.spouseOrParentName()
        );

        patient.setPreferredLanguage(
                request.preferredLanguage()
        );

        patient.setNeedsTranslator(
                request.needsTranslator()
        );

        if (request.request() != null) {

            patient.setRequest(
                    Patient.RequestInformation.builder()
                            .bookingStatus(
                                    request.request().bookingStatus()
                            )
                            .sourceOfRequest(
                                    request.request().sourceOfRequest()
                            )
                            .requestDate(
                                    request.request().requestDate()
                            )
                            .serviceRequested(
                                    request.request().serviceRequested()
                            )
                            .referral(
                                    request.request().referral()
                            )
                            .build()
            );
        } else {
            patient.setRequest(null);
        }

        patient.setFollowUpInterests(
                request.followUpInterests() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(
                        request.followUpInterests()
                )
        );

        if (request.noteLogs() != null) {
            patient.setNoteLogs(
                    new ArrayList<>(request.noteLogs())
            );
        } else {
            patient.setNoteLogs(
                    new ArrayList<>()
            );
        }
    }

    private Patient.Address toAddress(
            PatientRequest.Address address) {

        return Patient.Address.builder()
                .address(address.address())
                .city(address.city())
                .state(address.state())
                .zip(address.zip())
                .build();
    }
}