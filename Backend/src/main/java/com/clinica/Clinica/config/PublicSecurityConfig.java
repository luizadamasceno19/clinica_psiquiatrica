package com.clinica.Clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)
public class PublicSecurityConfig {

    @Bean
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        http
            // só estas rotas são públicas:
            .securityMatcher("/api/auth/**", "/api/medicos")
            .csrf().disable()
            .authorizeHttpRequests()
                .anyRequest().permitAll();
        return http.build();
    }
}
