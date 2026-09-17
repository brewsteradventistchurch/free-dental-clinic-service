package com.sda.dentalclinic.scheduling.service;

import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ScheduleAvailabilityService {

    Mono<LocalDate> findNextAvailableDate();
}