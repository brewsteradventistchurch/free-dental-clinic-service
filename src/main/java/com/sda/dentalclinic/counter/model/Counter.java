package com.sda.dentalclinic.counter.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("counters")
public class Counter {

    @Id
    private String id;

    private long sequence;
}