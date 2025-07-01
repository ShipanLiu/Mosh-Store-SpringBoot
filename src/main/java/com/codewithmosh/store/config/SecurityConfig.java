package com.codewithmosh.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security Configuration for the Store Application
 * 
 * This configuration sets up:
 * - Password encoding with BCrypt
 * - Basic security for REST API endpoints
 * - CORS configuration for frontend integration
 * - Public access to demo endpoints and documentation
 * 
 * @author Store Application Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Password encoder bean using BCrypt algorithm
     * BCrypt is the recommended password hashing algorithm for Spring Security
     * 
     * @return BCryptPasswordEncoder instance with default strength (10)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt with strength 12 for better security (default is 10)
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Security filter chain configuration
     * 
     * @param http HttpSecurity configuration object
     * @return SecurityFilterChain configured for the application
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF Configuration
            .csrf(csrf -> csrf
                .disable() // Disabled for REST API - enable if using session-based auth
            )
            
            // CORS Configuration
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
            )
            
            // Headers Configuration
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny) // Prevent clickjacking
                .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::and) // Prevent MIME sniffing
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000) // 1 year
                    .includeSubDomains(true)
                )
            )
            
            // Session Management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // REST API - no sessions
            )
            
            // Authorization Configuration
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers(
                    "/api/v1/",
                    "/api/v1/health",
                    "/api/v1/demo/**"
                ).permitAll()
                
                // Documentation endpoints - public in development
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).permitAll()
                
                // Static resources
                .requestMatchers(
                    "/static/**",
                    "/public/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                ).permitAll()
                
                // H2 Console (development only)
                .requestMatchers("/h2-console/**").permitAll()
                
                // Actuator endpoints (if enabled)
                .requestMatchers("/actuator/health").permitAll()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }

    /**
     * CORS configuration to allow frontend applications to access the API
     * 
     * @return CorsConfigurationSource with allowed origins, methods, and headers
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed origins (update for production)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",    // Local development
            "http://127.0.0.1:*",    // Local development
            "https://yourdomain.com" // Production domain
        ));
        
        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Exposed headers (headers that client can access)
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
} 