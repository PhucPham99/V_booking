package com.vbooking.backend.modules.auth.repository;

import com.vbooking.backend.modules.auth.entity.PasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetEntity, Long> {

    Optional<PasswordResetEntity> findByToken(String token);

    @Modifying
    @Query("DELETE FROM PasswordResetEntity p WHERE p.userId = :userId")
    void deleteByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM PasswordResetEntity p WHERE p.expiresAt < :dateTime")
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
