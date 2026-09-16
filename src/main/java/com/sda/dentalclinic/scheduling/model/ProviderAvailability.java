package com.sda.dentalclinic.scheduling.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("provider_availability")
@CompoundIndex(
        name = "provider_date_unique",
        def = "{'providerId': 1, 'date': 1}",
        unique = true
)
public class ProviderAvailability {

    @Id
    private String id;

    private String providerId;

    private LocalDate date;

    @Builder.Default
    private List<AvailabilityBlock> blocks = new ArrayList<>();


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityBlock {

        /**
         * Minutes since midnight.
         *
         * Example:
         * 08:00 = 480
         * 12:00 = 720
         */
        private int startMinutes;

        /**
         * Exclusive end of the availability block.
         *
         * Example:
         * 08:00 → 12:00
         * is represented as 480 → 720.
         */
        private int endMinutes;
    }
}