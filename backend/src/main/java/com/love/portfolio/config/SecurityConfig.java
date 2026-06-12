package com.love.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            .authorizeHttpRequests(authz -> authz
                // Allow public access to static frontend assets
                .requestMatchers("/", "/index.html", "/pages/**", "/assets/**").permitAll()
                // Allow public access to all APIs under /api/**
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                // Allow WebSocket connections
                .requestMatchers("/ws-location/**").permitAll()
                // Protect other APIs (require auth)
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {});
        
        return http.build();
    }
}