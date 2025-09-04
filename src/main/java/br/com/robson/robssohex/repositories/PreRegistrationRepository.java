package br.com.robson.robssohex.repositories;

import br.com.robson.robssohex.entities.PreRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PreRegistrationRepository extends JpaRepository<PreRegistration, UUID> {
    Optional<PreRegistration> findByIdAndUsedAtIsNullAndExpiresAtAfter(UUID id, Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM PreRegistration p WHERE p.expiresAt < :now AND p.usedAt IS NULL")
    int deleteExpiredUnused(@Param("now") Instant now);
}