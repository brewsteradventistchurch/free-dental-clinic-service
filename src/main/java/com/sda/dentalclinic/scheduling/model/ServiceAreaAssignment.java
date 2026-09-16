package com.sda.dentalclinic.scheduling.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("service_area_assignments")
@CompoundIndex(
        name = "service_area_date_unique",
        def = "{'serviceAreaId': 1, 'date': 1}",
        unique = true
)
public class ServiceAreaAssignment {

    @Id
    private String id;

    private String serviceAreaId;

    private String providerId;

    private LocalDate date;
}