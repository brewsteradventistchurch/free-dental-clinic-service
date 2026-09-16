package com.sda.dentalclinic.patient.repository;

import com.sda.dentalclinic.patient.model.Patient;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface PatientRepository
        extends ReactiveMongoRepository<Patient, String> {

    Mono<Patient> findByPatientNumber(String patientNumber);

}