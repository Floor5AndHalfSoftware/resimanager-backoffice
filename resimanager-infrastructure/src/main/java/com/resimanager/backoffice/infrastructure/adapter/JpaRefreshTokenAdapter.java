package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.RefreshToken;
import com.resimanager.backoffice.domain.port.out.RefreshTokenRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaRefreshTokenAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenRepository repository;

    @Override
    public RefreshToken guardar(RefreshToken token) {
        return repository.save(token);
    }

    @Override
    public Optional<RefreshToken> buscarPorHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public int revocarFamilia(UUID familyId) {
        return repository.revocarFamilia(familyId, OffsetDateTime.now());
    }

    @Override
    @Transactional
    public void revocarPorId(Integer id) {
        repository.findById(id).ifPresent(token -> {
            token.setRevocado("S");
            token.setEstMod("REFRESH-REVOKE");
            token.setFchHorMod(OffsetDateTime.now());
            repository.save(token);
        });
    }

    @Override
    @Transactional
    public int revocarTodasPorPersona(Integer personaId) {
        return repository.revocarPorPersona(personaId, OffsetDateTime.now());
    }

    @Override
    @Transactional
    public int eliminarExpirados(OffsetDateTime momento) {
        return repository.deleteByExpiraBefore(momento);
    }
}
