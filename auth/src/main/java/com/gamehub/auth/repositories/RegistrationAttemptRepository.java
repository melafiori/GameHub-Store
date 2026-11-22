package com.gamehub.auth.repositories;

import com.gamehub.auth.models.RegistrationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface RegistrationAttemptRepository extends JpaRepository<RegistrationAttempt, String> {
    Optional<RegistrationAttempt> findByEmail(String email);
    Optional<RegistrationAttempt> findByRut(String rut);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RegistrationAttempt r where r.registrationId = :id")
    Optional<RegistrationAttempt> findForUpdate(@Param("id") String id);
}
