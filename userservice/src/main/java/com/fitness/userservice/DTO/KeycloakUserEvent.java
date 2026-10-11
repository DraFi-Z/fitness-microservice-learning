package com.fitness.userservice.DTO;

public record KeycloakUserEvent(
        String keycloakId,
        String email,
        String firstName,
        String lastName
) {}

