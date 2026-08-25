package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);

    @Override
    public String codificar(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean coincide(String passwordCruda, String hash) {
        return encoder.matches(passwordCruda, hash);
    }
}