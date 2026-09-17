package com.sda.dentalclinic.scheduling.service;

import com.sda.dentalclinic.appointment.model.Appointment;
import com.sda.dentalclinic.appointment.repository.AppointmentRepository;
import com.sda.dentalclinic.configuration.model.ClinicConfiguration;
import com.sda.dentalclinic.configuration.service.ClinicConfigurationService;
import com.sda.dentalclinic.scheduling.model.ProviderAvailability;
import com.sda.dentalclinic.scheduling.model.ServiceAreaAssignment;
import com.sda.dentalclinic.scheduling.repository.ProviderAvailabilityRepository;
import com.sda.dentalclinic.scheduling.repository.ServiceAreaAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleAvailabilityServiceImpl
        implements ScheduleAvailabilityService {

    private static final int SLOT_MINUTES = 20;
    private static final int DAY_START = 7 * 60;
    private static final int DAY_END = 18 * 60;

    private final ProviderAvailabilityRepository availabilityRepository;
    private final ServiceAreaAssignmentRepository assignmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClinicConfigurationService configurationService;

    @Override
    public Mono<LocalDate> findNextAvailableDate() {
        LocalDate today = LocalDate.now();

        return configurationService
                .getConfiguration()
                .flatMap(configuration ->
                        availabilityRepository
                                .findByDateGreaterThanEqualOrderByDateAsc(
                                        today
                                )
                                .map(ProviderAvailability::getDate)
                                .distinct()
                                .concatMap(date ->
                                        dateHasAvailableSlot(
                                                date,
                                                today,
                                                configuration
                                        ).filter(Boolean::booleanValue)
                                                .map(ignored -> date)
                                )
                                .next()
                                .switchIfEmpty(Mono.just(today))
                );
    }

    private Mono<Boolean> dateHasAvailableSlot(
            LocalDate date,
            LocalDate today,
            ClinicConfiguration configuration) {

        int minimumStartMinutes =
                date.equals(today)
                        ? currentMinimumStartMinutes()
                        : DAY_START;

        return Mono.zip(
                assignmentRepository
                        .findByDate(date)
                        .collectList(),
                availabilityRepository
                        .findByDate(date)
                        .collectList(),
                appointmentRepository
                        .findByDate(date)
                        .collectList()
        ).map(data ->
                hasAvailableSlot(
                        date,
                        minimumStartMinutes,
                        configuration,
                        data.getT1(),
                        data.getT2(),
                        data.getT3()
                )
        );
    }

    private boolean hasAvailableSlot(
            LocalDate date,
            int minimumStartMinutes,
            ClinicConfiguration configuration,
            List<ServiceAreaAssignment> assignments,
            List<ProviderAvailability> availabilities,
            List<Appointment> appointments) {

        if (assignments.isEmpty() ||
                availabilities.isEmpty()) {

            return false;
        }

        for (ClinicConfiguration.ServiceArea serviceArea :
                configuration.getServiceAreas()) {

            if (!serviceArea.isActive()) {
                continue;
            }

            ServiceAreaAssignment assignment =
                    assignments.stream()
                            .filter(item ->
                                    serviceArea.getId()
                                            .equals(
                                                    item.getServiceAreaId()
                                            )
                            )
                            .findFirst()
                            .orElse(null);

            if (assignment == null ||
                    assignment.getProviderId() == null ||
                    assignment.getProviderId().isBlank()) {

                continue;
            }

            ClinicConfiguration.Provider provider =
                    configuration.getProviders()
                            .stream()
                            .filter(item ->
                                    item.getId().equals(
                                            assignment.getProviderId()
                                    ) &&
                                            item.isActive()
                            )
                            .findFirst()
                            .orElse(null);

            if (provider == null) {
                continue;
            }

            ProviderAvailability availability =
                    availabilities.stream()
                            .filter(item ->
                                    provider.getId().equals(
                                            item.getProviderId()
                                    )
                            )
                            .findFirst()
                            .orElse(null);

            if (availability == null ||
                    availability.getBlocks() == null ||
                    availability.getBlocks().isEmpty()) {

                continue;
            }

            List<ClinicConfiguration.Service> services =
                    configuration.getServices()
                            .stream()
                            .filter(ClinicConfiguration.Service::isActive)
                            .filter(service ->
                                    provider.getServiceIds()
                                            .contains(
                                                    service.getId()
                                            )
                            )
                            .toList();

            for (ClinicConfiguration.Service service :
                    services) {

                int durationMinutes =
                        Math.max(
                                SLOT_MINUTES,
                                service.getDefaultDurationMinutes()
                        );

                if (hasOpenSlot(
                        serviceArea.getId(),
                        provider.getId(),
                        availability,
                        appointments,
                        minimumStartMinutes,
                        durationMinutes
                )) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasOpenSlot(
            String serviceAreaId,
            String providerId,
            ProviderAvailability availability,
            List<Appointment> appointments,
            int minimumStartMinutes,
            int durationMinutes) {

        for (
                int startMinutes =
                        Math.max(
                                DAY_START,
                                ceilToSlot(
                                        minimumStartMinutes
                                )
                        );
                startMinutes < DAY_END;
                startMinutes += SLOT_MINUTES
        ) {

            int endMinutes =
                    startMinutes + durationMinutes;

            if (endMinutes > DAY_END) {
                continue;
            }

            if (!insideAvailability(
                    availability,
                    startMinutes,
                    endMinutes
            )) {
                continue;
            }

            if (hasConflict(
                    serviceAreaId,
                    providerId,
                    startMinutes,
                    durationMinutes,
                    appointments
            )) {
                continue;
            }

            return true;
        }

        return false;
    }

    private boolean insideAvailability(
            ProviderAvailability availability,
            int startMinutes,
            int endMinutes) {

        return availability.getBlocks()
                .stream()
                .anyMatch(block ->
                        startMinutes >=
                                block.getStartMinutes()
                                &&
                        endMinutes <=
                                block.getEndMinutes()
                );
    }

    private boolean hasConflict(
            String serviceAreaId,
            String providerId,
            int startMinutes,
            int durationMinutes,
            List<Appointment> appointments) {

        return appointments.stream()
                .filter(appointment ->
                        appointment.getServiceAreaId()
                                .equals(serviceAreaId)
                                ||
                        appointment.getProviderId()
                                .equals(providerId)
                )
                .anyMatch(appointment ->
                        overlaps(
                                appointment.getStartMinutes(),
                                appointment.getDurationMinutes(),
                                startMinutes,
                                durationMinutes
                        )
                );
    }

    private boolean overlaps(
            int existingStart,
            int existingDuration,
            int requestedStart,
            int requestedDuration) {

        int existingEnd =
                existingStart + existingDuration;

        int requestedEnd =
                requestedStart + requestedDuration;

        return requestedStart < existingEnd &&
                requestedEnd > existingStart;
    }

    private int currentMinimumStartMinutes() {
        LocalTime now = LocalTime.now();

        return now.getHour() * 60 +
                now.getMinute();
    }

    private int ceilToSlot(int minutes) {
        return (int) (
                Math.ceil(
                        (double) minutes /
                                SLOT_MINUTES
                ) * SLOT_MINUTES
        );
    }
}