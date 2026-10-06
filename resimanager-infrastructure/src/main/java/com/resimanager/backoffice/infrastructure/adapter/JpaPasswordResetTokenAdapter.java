package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.PasswordResetToken;
import com.resimanager.backoffice.domain.port.out.PasswordResetTokenRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPasswordResetTokenAdapter implements PasswordResetTokenRepositoryPort {

    private final PasswordResetTokenRepository repository;

    @Override
    public PasswordResetToken guardar(PasswordResetToken token) {
        return repository.save(token);
    }

    @Override
    public Optional<PasswordResetToken> buscarPorHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public int invalidarPorPersona(Integer personaId) {
        return repository.invalidarPorPersona(personaId, OffsetDateTime.now());
    }

    @Override
    @Transactional
    public int eliminarExpirados(OffsetDateTime momento) {
        return repository.deleteByExpiraBefore(momento);
    }
}
