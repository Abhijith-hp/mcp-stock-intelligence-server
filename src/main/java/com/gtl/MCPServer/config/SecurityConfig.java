package com.gtl.MCPServer.config;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration

public class SecurityConfig {

    public static final String LOGIN_LINK = "https://amservice1.fliplabs.net/authorize?redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin%2Foauth2%2Fcode%2Fcallback&client_id=01f047cc-77a1-1cbb-847d-d0d2f33e2c05&response_type=code";   
     public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"));
    }
    
    /**
     * Gets the current authenticated user ID
     * @return User ID or null if not authenticated
     */
    public static String getCurrentUserId() {
        if (!isAuthenticated()) {
            return null;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.sendRedirect(LOGIN_LINK);
            }
        };
    }
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> {
            authz
                .requestMatchers(
                    "/css/**",            
                    "/js/**",              
                    "/images/**",        
                    "/sse", "/sse/**",
                    "/mcp/message", "/mcp/tools",
                    "/login/**", "/error", "/actuator/**"
                ).permitAll()
                .anyRequest().authenticated();
        })
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(eh -> eh.authenticationEntryPoint(customAuthenticationEntryPoint()));

    return http.build();
}


 

}
