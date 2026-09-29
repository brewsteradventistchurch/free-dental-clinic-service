package com.sda.dentalclinic.user.model;

import java.time.Instant;

public record AdminUserResponse(
        String id,
        String email,
        String firstName,
        String lastName,
        Role role,
        Status status,
        boolean active,
        Instant createdDate,
        Instant approvedAt,
        Instant lastLogin
) {
}