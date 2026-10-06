package com.resimanager.backoffice.infrastructure.persistence.repository;

import com.resimanager.backoffice.domain.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE PasswordResetToken t SET t.usado = :momento, t.estMod = 'PWD-RESET-INVALIDATE', "
            + "t.fchHorMod = :momento WHERE t.personaId = :personaId AND t.usado IS NULL")
    int invalidarPorPersona(@Param("personaId") Integer personaId, @Param("momento") OffsetDateTime momento);

    int deleteByExpiraBefore(OffsetDateTime momento);
}
