package com.sda.dentalclinic.scheduling.controller;

import com.sda.dentalclinic.scheduling.service.ScheduleAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleAvailabilityController {

    private final ScheduleAvailabilityService
            scheduleAvailabilityService;

    @GetMapping("/next-available-date")
    public Mono<NextAvailableDateResponse>
    getNextAvailableDate() {

        return scheduleAvailabilityService
                .findNextAvailableDate()
                .map(NextAvailableDateResponse::new);
    }

    public record NextAvailableDateResponse(
            LocalDate date
    ) {
    }
}