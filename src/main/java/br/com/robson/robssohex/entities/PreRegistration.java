package br.com.robson.robssohex.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pre_registration",
        indexes = {
                @Index(name = "idx_pre_reg_email", columnList = "email"),
                @Index(name = "idx_pre_reg_expires", columnList = "expiresAt")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pre_reg_username_email", columnNames = {"username","email"})
        }
)
@Getter
@Setter
public class PreRegistration {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, length = 120)
    private String username;

    @Column(nullable = false, length = 190)
    private String email;

    @Column(nullable = false, length = 100) // hash BCrypt/Argon2 do token
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column
    private Instant usedAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(length = 64)
    private String createdIp;

    @Column(length = 255)
    private String createdUserAgent;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

}
