package com.sda.dentalclinic.scheduling.controller;

import com.sda.dentalclinic.scheduling.model.ServiceAreaAssignment;
import com.sda.dentalclinic.scheduling.service.ServiceAreaAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/service-area-assignments")
@RequiredArgsConstructor
public class ServiceAreaAssignmentController {

    private final ServiceAreaAssignmentService assignmentService;

    @GetMapping
    public Flux<ServiceAreaAssignment> findByDate(
            @RequestParam LocalDate date) {

        return assignmentService.findByDate(date);
    }

    @GetMapping("/{serviceAreaId}")
    public Mono<ServiceAreaAssignment> findByServiceAreaAndDate(
            @PathVariable String serviceAreaId,
            @RequestParam LocalDate date) {

        return assignmentService.findByServiceAreaAndDate(
                serviceAreaId,
                date
        );
    }

    @PutMapping
    public Mono<ServiceAreaAssignment> assign(
            @RequestParam String serviceAreaId,
            @RequestParam String providerId,
            @RequestParam LocalDate date) {

        return assignmentService.assign(
                serviceAreaId,
                providerId,
                date
        );
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(
            @RequestParam String serviceAreaId,
            @RequestParam LocalDate date) {

        return assignmentService.delete(
                serviceAreaId,
                date
        );
    }
}