package com.resimanager.backoffice.infrastructure.persistence.repository;

import com.resimanager.backoffice.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE RefreshToken t SET t.revocado = 'S', t.estMod = 'REFRESH-REVOKE', "
            + "t.fchHorMod = :momento WHERE t.familyId = :familyId AND t.revocado = 'N'")
    int revocarFamilia(@Param("familyId") UUID familyId, @Param("momento") OffsetDateTime momento);

    int deleteByExpiraBefore(OffsetDateTime momento);
}
