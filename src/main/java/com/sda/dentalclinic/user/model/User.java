package com.sda.dentalclinic.user.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String googleId;

    @Indexed(unique = true)
    private String email;

    private String firstName;

    private String lastName;

    private Role role;

    private Status status;

    private boolean active;

    private Instant createdDate;

    private Instant approvedAt;

    private Instant lastLogin;

}