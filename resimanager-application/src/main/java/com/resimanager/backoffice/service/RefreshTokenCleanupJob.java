package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.port.in.RefreshTokenUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Limpieza programada de refresh tokens expirados.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupJob {

    private final RefreshTokenUseCase refreshTokenUseCase;

    @Scheduled(cron = "${app.security.refresh-cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void limpiarExpirados() {
        int eliminados = refreshTokenUseCase.eliminarExpirados();
        if (eliminados > 0) {
            log.info("Refresh tokens expirados eliminados: {}", eliminados);
        }
    }
}
