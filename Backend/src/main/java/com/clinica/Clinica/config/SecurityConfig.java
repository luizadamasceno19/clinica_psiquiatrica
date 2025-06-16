// src/main/java/com/clinica/Clinica/config/SecurityConfig.java
package com.clinica.Clinica.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Order(2)  // Executa APÓS o JwtWebSecurityConfig (Order 1)
public class SecurityConfig {

    @Bean(name = "defaultFilterChain")
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            // Este matcher pega tudo (inclusive /api/**) — mas só será chamado
            // depois que o Order(1) não casar.
            .securityMatcher("/**")
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                // aqui você pode liberar URLs públicas abaixo, se quiser:
                // .requestMatchers("/publico/**").permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
        	    "http://localhost:5173",
        	    "https://proud-island-01c509b1e.6.azurestaticapps.net"
        	));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
