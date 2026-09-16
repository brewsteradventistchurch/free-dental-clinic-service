package com.sda.dentalclinic.appointment.service;

import com.sda.dentalclinic.appointment.dto.AppointmentRequest;
import com.sda.dentalclinic.appointment.model.Appointment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface AppointmentService {

    Flux<Appointment> findByDate(LocalDate date);

    Mono<Appointment> findById(String id);

    Mono<Appointment> create(AppointmentRequest request);

    Mono<Appointment> update(
            String appointmentId,
            AppointmentRequest request);

    Mono<Void> delete(String appointmentId);
}