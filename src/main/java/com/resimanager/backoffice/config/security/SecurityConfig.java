package com.resimanager.backoffice.config.security;

import com.resimanager.backoffice.config.security.jwt.JWTAuthorizationFilter;
import com.resimanager.backoffice.config.security.provider.CustomAuthenticationProvider;
import com.resimanager.backoffice.exception.MvcRequestMatcherConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Collections;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;
import static com.resimanager.backoffice.utils.Constants.LOGIN_PATH;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    private final String[] WHITE_LIST = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger*/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/api-docs/**",
            "/webjars/**",
            "/console/**",
            "/error",
            "/favicon.ico",
            API_VERSION_PATH + LOGIN_PATH
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    try {
                        for (String pattern : WHITE_LIST) {
                            auth.requestMatchers(new MvcRequestMatcher(introspector, pattern)).permitAll();
                        }
                    } catch (Exception e) {
                        throw new MvcRequestMatcherConfigurationException("Failed to configure MVC request matchers", e);
                    }
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(customAuthenticationProvider));
    }
}
