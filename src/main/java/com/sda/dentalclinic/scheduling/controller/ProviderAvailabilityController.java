package com.sda.dentalclinic.scheduling.controller;

import com.sda.dentalclinic.scheduling.model.ProviderAvailability;
import com.sda.dentalclinic.scheduling.service.ProviderAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/provider-availability")
@RequiredArgsConstructor
public class ProviderAvailabilityController {

    private final ProviderAvailabilityService availabilityService;

    @GetMapping
    public Flux<ProviderAvailability> find(
            @RequestParam(required = false) LocalDate date) {

        if (date == null) {
            return availabilityService.findAll();
        }

        return availabilityService.findByDate(date);
    }

    @GetMapping("/{providerId}")
    public Mono<ProviderAvailability> findByProviderAndDate(
            @PathVariable String providerId,
            @RequestParam LocalDate date) {

        return availabilityService.findByProviderAndDate(
                providerId,
                date
        );
    }

    @PutMapping
    public Mono<ProviderAvailability> save(
            @RequestBody ProviderAvailability availability) {

        return availabilityService.save(availability);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(
            @RequestParam String providerId,
            @RequestParam LocalDate date) {

        return availabilityService.delete(
                providerId,
                date
        );
    }
}