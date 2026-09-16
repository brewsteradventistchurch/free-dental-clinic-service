
//    @Override
/// /    public Mono<Appointment> findById(String id) {
//        return appointmentRepository.findById(id);
//    }
package com.sda.dentalclinic.appointment.service;

import com.sda.dentalclinic.appointment.dto.AppointmentRequest;
import com.sda.dentalclinic.appointment.model.Appointment;
import com.sda.dentalclinic.appointment.repository.AppointmentRepository;
import com.sda.dentalclinic.configuration.model.ClinicConfiguration;
import com.sda.dentalclinic.configuration.service.ClinicConfigurationService;
import com.sda.dentalclinic.scheduling.model.ProviderAvailability;
import com.sda.dentalclinic.scheduling.model.ServiceAreaAssignment;
import com.sda.dentalclinic.scheduling.repository.ProviderAvailabilityRepository;
import com.sda.dentalclinic.scheduling.repository.ServiceAreaAssignmentRepository;
import com.sda.dentalclinic.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl
        implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ClinicConfigurationService configurationService;
    private final ServiceAreaAssignmentRepository assignmentRepository;
    private final ProviderAvailabilityRepository availabilityRepository;

    @Override
    public Flux<Appointment> findByDate(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }

    @Override
    public Mono<Appointment> findById(String id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public Mono<Appointment> create(
            AppointmentRequest request) {

        return validateAndResolve(
                request,
                null
        ).flatMap(resolved -> {

            Appointment appointment =
                    Appointment.builder()
                            .patientId(request.patientId())
                            .serviceAreaId(request.serviceAreaId())
                            .providerId(resolved.providerId())
                            .serviceId(request.serviceId())
                            .date(request.date())
                            .startMinutes(request.startMinutes())
                            .durationMinutes(request.durationMinutes())
                            .bookingStatus(request.bookingStatus())
                            .build();

            return appointmentRepository.save(appointment);
        });
    }

    @Override
    public Mono<Appointment> update(
            String appointmentId,
            AppointmentRequest request) {

        return appointmentRepository.findById(appointmentId)
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "Appointment does not exist: "
                                                + appointmentId
                                )
                        )
                )
                .flatMap(existing ->
                        validateAndResolve(
                                request,
                                appointmentId
                        ).map(resolved -> {

                            existing.setPatientId(
                                    request.patientId()
                            );

                            existing.setServiceAreaId(
                                    request.serviceAreaId()
                            );

                            existing.setProviderId(
                                    resolved.providerId()
                            );

                            existing.setServiceId(
                                    request.serviceId()
                            );

                            existing.setDate(
                                    request.date()
                            );

                            existing.setStartMinutes(
                                    request.startMinutes()
                            );

                            existing.setDurationMinutes(
                                    request.durationMinutes()
                            );

                            existing.setBookingStatus(
                                    request.bookingStatus()
                            );

                            return existing;
                        })
                )
                .flatMap(appointmentRepository::save);
    }

    @Override
    public Mono<Void> delete(String appointmentId) {

        return appointmentRepository.deleteById(appointmentId);
    }

    private Mono<ResolvedAssignment> validateAndResolve(
            AppointmentRequest request,
            String excludedAppointmentId) {

        return validateBasicTime(request)
                .then(
                        patientRepository.findById(
                                request.patientId()
                        )
                )
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "Patient does not exist: "
                                                + request.patientId()
                                )
                        )
                )
                .then(
                        configurationService.getConfiguration()
                )
                .flatMap(configuration ->
                        resolveConfiguration(
                                configuration,
                                request
                        )
                )
                .flatMap(resolved ->
                        validateAssignmentAndAvailability(
                                request,
                                resolved
                        )
                )
                .flatMap(resolved ->
                        validateConflicts(
                                request,
                                resolved.providerId(),
                                excludedAppointmentId
                        )
                );
    }

    private Mono<Void> validateBasicTime(
            AppointmentRequest request) {

        int endMinutes =
                request.startMinutes()
                        + request.durationMinutes();

        if (request.startMinutes() < 0) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Start time cannot be negative."
                    )
            );
        }

        if (endMinutes > 24 * 60) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Appointment cannot extend past midnight."
                    )
            );
        }

        return Mono.empty();
    }

    private Mono<ResolvedAssignment> resolveConfiguration(
            ClinicConfiguration configuration,
            AppointmentRequest request) {

        ClinicConfiguration.Service service =
                configuration.getServices()
                        .stream()
                        .filter(item ->
                                item.getId().equals(
                                        request.serviceId()
                                )
                        )
                        .findFirst()
                        .orElse(null);

        if (service == null || !service.isActive()) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Service does not exist or is inactive: "
                                    + request.serviceId()
                    )
            );
        }

        ClinicConfiguration.ServiceArea serviceArea =
                configuration.getServiceAreas()
                        .stream()
                        .filter(item ->
                                item.getId().equals(
                                        request.serviceAreaId()
                                )
                        )
                        .findFirst()
                        .orElse(null);

        if (serviceArea == null || !serviceArea.isActive()) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Service area does not exist or is inactive: "
                                    + request.serviceAreaId()
                    )
            );
        }

        ClinicConfiguration.BookingStatus bookingStatus =
                configuration.getBookingStatuses()
                        .stream()
                        .filter(item ->
                                item.getId().equals(
                                        request.bookingStatus()
                                )
                        )
                        .findFirst()
                        .orElse(null);

        if (bookingStatus == null || !bookingStatus.isActive()) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Booking status does not exist or is inactive: "
                                    + request.bookingStatus()
                    )
            );
        }

        return assignmentRepository
                .findByServiceAreaIdAndDate(
                        request.serviceAreaId(),
                        request.date()
                )
                .switchIfEmpty(
                        Mono.error(
                                new IllegalStateException(
                                        "No provider is assigned to "
                                                + "this service area on "
                                                + "the selected date."
                                )
                        )
                )
                .map(assignment ->
                        new ResolvedAssignment(
                                assignment,
                                service
                        )
                );
    }

    private Mono<ResolvedAssignment> validateAssignmentAndAvailability(
            AppointmentRequest request,
            ResolvedAssignment resolved) {

        return configurationService.getConfiguration()
                .flatMap(configuration -> {

                    ClinicConfiguration.Provider provider =
                            configuration.getProviders()
                                    .stream()
                                    .filter(item ->
                                            item.getId().equals(
                                                    resolved.providerId()
                                            )
                                    )
                                    .findFirst()
                                    .orElse(null);

                    if (provider == null || !provider.isActive()) {
                        return Mono.error(
                                new IllegalStateException(
                                        "Assigned provider does not "
                                                + "exist or is inactive."
                                )
                        );
                    }

                    if (!provider.getServiceIds()
                            .contains(request.serviceId())) {

                        return Mono.error(
                                new IllegalStateException(
                                        "Assigned provider does not "
                                                + "provide the selected "
                                                + "service."
                                )
                        );
                    }

                    return availabilityRepository
                            .findByProviderIdAndDate(
                                    provider.getId(),
                                    request.date()
                            )
                            .switchIfEmpty(
                                    Mono.error(
                                            new IllegalStateException(
                                                    "Provider has no "
                                                            + "availability "
                                                            + "on the selected "
                                                            + "date."
                                            )
                                    )
                            )
                            .flatMap(availability -> {

                                int endMinutes =
                                        request.startMinutes()
                                                + request.durationMinutes();

                                boolean available =
                                        contains(
                                                availability,
                                                request.startMinutes(),
                                                endMinutes
                                        );

                                if (!available) {
                                    return Mono.error(
                                            new IllegalStateException(
                                                    "Appointment is outside "
                                                            + "the provider's "
                                                            + "availability."
                                            )
                                    );
                                }

                                return Mono.just(resolved);
                            });
                });
    }

    private Mono<ResolvedAssignment> validateConflicts(
            AppointmentRequest request,
            String providerId,
            String excludedAppointmentId) {

        return appointmentRepository
                .findByDate(request.date())
                .filter(appointment ->
                        !appointment.getId().equals(excludedAppointmentId)
                )
                .filter(appointment ->
                        overlaps(
                                appointment,
                                request.startMinutes(),
                                request.durationMinutes()
                        )
                )
                .flatMap(appointment -> {

                    if (appointment.getServiceAreaId()
                            .equals(request.serviceAreaId())) {

                        return Mono.error(
                                new IllegalStateException(
                                        "The service area is already "
                                                + "occupied during this time."
                                )
                        );
                    }

                    if (appointment.getProviderId()
                            .equals(providerId)) {

                        return Mono.error(
                                new IllegalStateException(
                                        "The provider is already "
                                                + "scheduled during this time."
                                )
                        );
                    }

                    return Mono.<Void>empty();
                })
                .then(
                        Mono.just(
                                new ResolvedAssignment(
                                        null,
                                        null,
                                        providerId
                                )
                        )
                );
    }

    private boolean contains(
            ProviderAvailability availability,
            int startMinutes,
            int endMinutes) {

        if (availability.getBlocks() == null) {
            return false;
        }

        return availability.getBlocks()
                .stream()
                .anyMatch(block ->
                        startMinutes >= block.getStartMinutes()
                                && endMinutes <= block.getEndMinutes()
                );
    }

    private boolean overlaps(
            Appointment appointment,
            int startMinutes,
            int durationMinutes) {

        int existingStart =
                appointment.getStartMinutes();

        int existingEnd =
                existingStart
                        + appointment.getDurationMinutes();

        int requestedEnd =
                startMinutes + durationMinutes;

        return startMinutes < existingEnd
                && requestedEnd > existingStart;
    }

    private record ResolvedAssignment(
            ServiceAreaAssignment assignment,
            ClinicConfiguration.Service service,
            String providerId) {

        private ResolvedAssignment(
                ServiceAreaAssignment assignment,
                ClinicConfiguration.Service service) {

            this(
                    assignment,
                    service,
                    assignment.getProviderId()
            );
        }
    }
}