package com.chronos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. THE PASSWORD ENCODER
    // This allows you to Autowire 'PasswordEncoder' in your Controller/Service.
    // It turns "password123" into "$2a$10$wT..." (Secure Hash)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. THE SECURITY FILTER CHAIN
    // This defines "Who can access What".
    // For a student project, we keep it "Permissive" to avoid 403 Errors.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery) protection.
                // Why? Because it blocks simple POST requests from HTML forms/Postman.
                .csrf(csrf -> csrf.disable())

                // Define URL permissions
                .authorizeHttpRequests(auth -> auth
                        // Allow everyone to access the Login, Signup, and H2 Console
                        .requestMatchers("/api/**", "/h2-console/**", "/**").permitAll()
                        // Require authentication for nothing (We handle auth manually in Controller)
                        .anyRequest().permitAll()
                )

                // Allow frames (needed if you use H2 Database Console)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}