package com.sda.dentalclinic.patient.service;

import com.sda.dentalclinic.patient.dto.PatientRequest;
import com.sda.dentalclinic.patient.model.Patient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PatientService {

    Mono<Patient> findById(String id);

    Mono<Patient> findByPatientNumber(String patientNumber);

    Flux<Patient> findAll();

    Flux<Patient> search(String query);

    Mono<Patient> create(PatientRequest request);

    Mono<Patient> update(String id, PatientRequest request);
}