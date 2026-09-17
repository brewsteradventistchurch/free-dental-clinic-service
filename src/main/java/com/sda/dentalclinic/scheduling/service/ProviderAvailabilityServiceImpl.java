package com.sda.dentalclinic.scheduling.service;

import com.sda.dentalclinic.configuration.model.ClinicConfiguration;
import com.sda.dentalclinic.configuration.service.ClinicConfigurationService;
import com.sda.dentalclinic.scheduling.model.ProviderAvailability;
import com.sda.dentalclinic.scheduling.repository.ProviderAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderAvailabilityServiceImpl
        implements ProviderAvailabilityService {

    private final ProviderAvailabilityRepository repository;
    private final ClinicConfigurationService configurationService;

    @Override
    public Flux<ProviderAvailability> findAll() {
        return repository.findAll()
                .sort(
                        //(left, right) -> left.getDate().compareTo(right.getDate())
                        Comparator.comparing(ProviderAvailability::getDate)
                );
    }

    @Override
    public Flux<ProviderAvailability> findByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    @Override
    public Mono<ProviderAvailability> findByProviderAndDate(
            String providerId,
            LocalDate date) {

        return repository.findByProviderIdAndDate(
                providerId,
                date
        );
    }

    @Override
    public Mono<ProviderAvailability> save(
            ProviderAvailability availability) {

        return validateAvailability(availability)
                .then(
                        repository
                                .findByProviderIdAndDate(
                                        availability.getProviderId(),
                                        availability.getDate()
                                )
                                .defaultIfEmpty(
                                        ProviderAvailability.builder()
                                                .providerId(
                                                        availability
                                                                .getProviderId()
                                                )
                                                .date(
                                                        availability
                                                                .getDate()
                                                )
                                                .build()
                                )
                )
                .flatMap(existing -> {

                    existing.setProviderId(
                            availability.getProviderId()
                    );

                    existing.setDate(
                            availability.getDate()
                    );

                    List<ProviderAvailability.AvailabilityBlock> blocks =
                            availability.getBlocks() == null
                                    ? new ArrayList<>()
                                    : new ArrayList<>(
                                    availability.getBlocks()
                            );

                    blocks.sort(
                            Comparator.comparingInt(
                                    ProviderAvailability.AvailabilityBlock
                                            ::getStartMinutes
                            )
                    );

                    validateNoOverlappingBlocks(blocks);

                    existing.setBlocks(blocks);

                    return repository.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(
            String providerId,
            LocalDate date) {

        return repository.deleteByProviderIdAndDate(
                providerId,
                date
        );
    }

    @Override
    public boolean contains(
            ProviderAvailability availability,
            int startMinutes,
            int endMinutes) {

        if (availability == null ||
                availability.getBlocks() == null) {
            return false;
        }

        return availability.getBlocks()
                .stream()
                .anyMatch(block ->
                        startMinutes >= block.getStartMinutes()
                                && endMinutes <= block.getEndMinutes()
                );
    }

    @Override
    public Flux<ProviderAvailability> findDatesOnOrAfter(
            LocalDate date
    ) {
        return repository.findByDateGreaterThanEqualOrderByDateAsc(date);
    }

    private Mono<Void> validateAvailability(
            ProviderAvailability availability) {

        if (availability == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Availability is required."
                    )
            );
        }

        if (availability.getProviderId() == null ||
                availability.getProviderId().isBlank()) {

            return Mono.error(
                    new IllegalArgumentException(
                            "Provider ID is required."
                    )
            );
        }

        if (availability.getDate() == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Date is required."
                    )
            );
        }

        if (availability.getBlocks() == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Availability blocks are required."
                    )
            );
        }

        return configurationService.getConfiguration()
                .flatMap(configuration -> {

                    ClinicConfiguration.Provider provider =
                            configuration.getProviders()
                                    .stream()
                                    .filter(item ->
                                            item.getId().equals(
                                                    availability.getProviderId()
                                            )
                                    )
                                    .findFirst()
                                    .orElse(null);

                    if (provider == null) {
                        return Mono.error(
                                new IllegalArgumentException(
                                        "Provider does not exist: "
                                                + availability.getProviderId()
                                )
                        );
                    }

                    if (!provider.isActive()) {
                        return Mono.error(
                                new IllegalStateException(
                                        "Provider is inactive: "
                                                + availability.getProviderId()
                                )
                        );
                    }

                    return Mono.empty();
                });
    }

    private void validateNoOverlappingBlocks(
            List<ProviderAvailability.AvailabilityBlock> blocks) {

        for (ProviderAvailability.AvailabilityBlock block : blocks) {

            if (block.getStartMinutes() < 0 ||
                    block.getEndMinutes() > 24 * 60 ||
                    block.getStartMinutes() >= block.getEndMinutes()) {

                throw new IllegalArgumentException(
                        "Each availability block must have a valid " +
                                "start and end time."
                );
            }
        }

        for (int i = 1; i < blocks.size(); i++) {

            ProviderAvailability.AvailabilityBlock previous =
                    blocks.get(i - 1);

            ProviderAvailability.AvailabilityBlock current =
                    blocks.get(i);

            if (current.getStartMinutes()
                    < previous.getEndMinutes()) {

                throw new IllegalArgumentException(
                        "Availability blocks cannot overlap."
                );
            }
        }
    }
}