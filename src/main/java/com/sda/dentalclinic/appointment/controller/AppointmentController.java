package com.sda.dentalclinic.appointment.controller;

import com.sda.dentalclinic.appointment.dto.AppointmentRequest;
import com.sda.dentalclinic.appointment.dto.AppointmentResponse;
import com.sda.dentalclinic.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public Flux<AppointmentResponse> findByDate(
            @RequestParam LocalDate date) {

        return appointmentService.findByDate(date)
                .map(AppointmentResponse::from);
    }

    @PostMapping
    public Mono<AppointmentResponse> create(
            @Valid @RequestBody AppointmentRequest request) {

        return appointmentService.create(request)
                .map(AppointmentResponse::from);
    }

    @PutMapping("/{id}")
    public Mono<AppointmentResponse> update(
            @PathVariable String id,
            @Valid @RequestBody AppointmentRequest request) {

        return appointmentService.update(id, request)
                .map(AppointmentResponse::from);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(
            @PathVariable String id) {

        return appointmentService.delete(id);
    }
}