package com.sda.dentalclinic.appointment.repository;

import com.sda.dentalclinic.appointment.model.Appointment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface AppointmentRepository
        extends ReactiveMongoRepository<Appointment, String> {

    Flux<Appointment> findByDate(LocalDate date);

    Flux<Appointment> findByDateAndProviderId(
            LocalDate date,
            String providerId
    );
}