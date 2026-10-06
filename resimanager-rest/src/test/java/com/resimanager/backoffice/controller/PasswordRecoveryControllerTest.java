package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.port.in.PasswordRecoveryUseCase;
import com.resimanager.backoffice.controller.handler.GeneralControllerExceptionHandler;
import com.resimanager.backoffice.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PasswordRecoveryControllerTest {

    private PasswordRecoveryUseCase useCase;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        useCase = mock(PasswordRecoveryUseCase.class);
        PasswordRecoveryController controller = new PasswordRecoveryController(useCase);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GeneralControllerExceptionHandler())
                .build();
    }

    @Test
    void forgotPasswordRespondeSiempre200() throws Exception {
        mvc.perform(post("/v1/auth/forgot-password")
                        .contentType("application/json")
                        .content("{\"email\":\"user@x.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void resetPasswordValidoResponde200() throws Exception {
        mvc.perform(post("/v1/auth/reset-password")
                        .contentType("application/json")
                        .content("{\"token\":\"tok\",\"password\":\"nuevaClave1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void resetPasswordConTokenInvalidoResponde400() throws Exception {
        doThrow(new ServiceException("Token inválido o expirado.", 400))
                .when(useCase).restablecer(anyString(), anyString());

        mvc.perform(post("/v1/auth/reset-password")
                        .contentType("application/json")
                        .content("{\"token\":\"malo\",\"password\":\"nuevaClave1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPasswordDebilResponde400() throws Exception {
        doThrow(new ServiceException("La contraseña no cumple la política mínima (8 caracteres).", 400))
                .when(useCase).restablecer(anyString(), anyString());

        mvc.perform(post("/v1/auth/reset-password")
                        .contentType("application/json")
                        .content("{\"token\":\"tok\",\"password\":\"corta\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void forgotPasswordExcedidoResponde429() throws Exception {
        doThrow(new ServiceException("Demasiadas solicitudes.", 429))
                .when(useCase).solicitar(any());

        mvc.perform(post("/v1/auth/forgot-password")
                        .contentType("application/json")
                        .content("{\"email\":\"user@x.com\"}"))
                .andExpect(status().isTooManyRequests());
    }
}
