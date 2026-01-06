package com.hms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Main security configuration for the application.
 * Defines who can access which endpoints and how authentication works.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * Configures the security rules for all HTTP requests.
     * Sets up endpoint permissions, CORS, and JWT authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT authentication)
                .csrf(csrf -> csrf.disable())
                
                // Enable CORS for frontend to access API
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Define access rules for different endpoints
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - anyone can access
                        .requestMatchers("/api/auth/**").permitAll()  // Login, register
                        
                        // Admin-only endpoints
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        
                        // Multi-role endpoints - ADMIN, MANAGER, or RECEPTIONIST
                        .requestMatchers("/api/payments/**").hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST")
                        .requestMatchers("/api/bookings/**").hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST")
                        .requestMatchers("/api/guests/**").hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST")
                        
                        // Admin and Manager only
                        .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Rooms - any authenticated user
                        .requestMatchers("/api/rooms/**").authenticated()
                        
                        // All other endpoints require authentication
                        .anyRequest().authenticated())
                
                // Use stateless sessions (no server-side session storage)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Set custom authentication provider
                .authenticationProvider(authenticationProvider)
                
                // Add JWT filter before Spring's authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures CORS to allow frontend applications to access the API.
     * Allows requests from any origin with any headers and methods.
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOriginPattern("*");  // Allow all origins 
        config.addAllowedHeader("*");  // Allow all headers 
        config.addAllowedMethod("*");  // Allow all HTTP methods (GET, POST, PUT, DELETE)
        config.setAllowCredentials(true);  // Allow cookies and credentials

        // Apply CORS config to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}