package com.resimanager.backoffice.config.security;

import com.resimanager.backoffice.config.security.jwt.JWTAuthorizationFilter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            "/",
            "/scalar/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/api-docs/**",
            "/console/**",
            "/error",
            "/favicon.ico",
            API_VERSION_PATH + LOGIN_PATH
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    try {
                        auth.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll();
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // Allow all origins in development
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Perfil-Id"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(customAuthenticationProvider));
    }
}
