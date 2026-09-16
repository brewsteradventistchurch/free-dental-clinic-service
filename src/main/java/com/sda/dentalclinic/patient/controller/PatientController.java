package com.sda.dentalclinic.patient.controller;

import com.sda.dentalclinic.patient.dto.PatientRequest;
import com.sda.dentalclinic.patient.dto.PatientResponse;
import com.sda.dentalclinic.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/{id}")
    public Mono<PatientResponse> findById(
            @PathVariable String id) {

        return patientService.findById(id)
                .map(PatientResponse::from);
    }

    @GetMapping
    public Flux<PatientResponse> findPatients(
            @RequestParam(required = false) String search) {

        if (search == null || search.isBlank()) {
            return patientService.findAll()
                    .map(PatientResponse::from);
        }

        return patientService.search(search)
                .map(PatientResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PatientResponse> create(
            @Valid @RequestBody PatientRequest request) {

        return patientService.create(request)
                .map(PatientResponse::from);
    }

    @PutMapping("/{id}")
    public Mono<PatientResponse> update(
            @PathVariable String id,
            @Valid @RequestBody PatientRequest request) {

        return patientService.update(id, request)
                .map(PatientResponse::from);
    }
}