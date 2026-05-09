package com.resimanager.backoffice.config.security.provider;

import com.resimanager.backoffice.exception.ServiceException;
import com.resimanager.backoffice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static com.resimanager.backoffice.utils.Constants.LOGIN_ATTEMPTS_CACHE;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("cacheManagerLogin")
    private CacheManager cacheManagerLogin;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        final String username = authentication.getName();
        final var attemps = Objects.requireNonNull(cacheManagerLogin.getCache(LOGIN_ATTEMPTS_CACHE)).get(username);
        var attempsValue = 0;

        checkIfNumberOfPossibleAttemptsReached(attemps);

        var user = userService.loadUserByUsername(username);

        if (user != null) {
            var apiPass = new String(Base64.getDecoder().decode((String) authentication.getCredentials()));
            var dbPass = user.getPassword();

            if (username.equals(user.getUsername()) && passwordEncoder.matches(apiPass, dbPass)) {
                Objects.requireNonNull(cacheManagerLogin.getCache(LOGIN_ATTEMPTS_CACHE)).evict(username);

                final List<GrantedAuthority> authorities = new ArrayList<>();
                user.getAuthorities().forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority)));

                return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), authorities);
            } else {

                if (Objects.isNull(attemps)) {
                    Objects.requireNonNull(cacheManagerLogin.getCache(LOGIN_ATTEMPTS_CACHE)).put(username, 1);
                    throw new BadCredentialsException("Incorrect username or password!");
                }

                attempsValue = (Integer) Objects.requireNonNull(attemps.get());
                if (attempsValue < 5) {
                    Objects.requireNonNull(cacheManagerLogin.getCache(LOGIN_ATTEMPTS_CACHE)).put(username, attempsValue + 1);
                    throw new BadCredentialsException("Incorrect username or password!");
                }

                throw new BadCredentialsException("Number of possible attempts reached!");
            }
        } else {
            throw new ServiceException("User not found!", 404);
        }
    }

    private static void checkIfNumberOfPossibleAttemptsReached(Cache.ValueWrapper attemps) {
        if (Objects.nonNull(attemps)) {
            var attempsValue = (Integer) Objects.requireNonNull(attemps.get());
            if (attempsValue >= 5) {
                throw new BadCredentialsException("Number of possible attempts reached!");
            }
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}
