package br.com.robson.robssohex.interactors.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserModel(
        UUID id,
        String username,
        String email,
        LocalDate dateOfBirth,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt) {}