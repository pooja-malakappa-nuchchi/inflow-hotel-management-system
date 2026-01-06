package com.hms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filter that checks JWT tokens on every request to authenticate users.
 * Runs before each request reaches your controllers to verify the user's identity.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Intercepts every request to check if it has a valid JWT token.
     * If valid, authenticates the user for this request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Get the Authorization header from request (contains JWT token)
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);  // No token, continue without authentication
            return;
        }

        // Extract token by removing "Bearer " prefix (7 characters)
        jwt = authHeader.substring(7);
        
        // Get user email from the token
        userEmail = jwtUtils.extractUsername(jwt);

        // If we found an email and user is not already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Load user details from database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            // Verify if token is valid for this user
            if (jwtUtils.validateToken(jwt, userDetails)) {
                
                // Create authentication token with user details and roles
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                // Add request details to authentication
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Save authentication in security context (user is now logged in for this request)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Continue to next filter or controller
        filterChain.doFilter(request, response);
    }
}