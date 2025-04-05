package com.services.wallet.infra.security;

import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.web.*;
import org.springframework.security.web.context.*;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    private final ServiceSecretAuthenticationFilter serviceSecretAuthenticationFilter;
    public SecurityConfig(ServiceSecretAuthenticationFilter serviceSecretAuthenticationFilter) {
        this.serviceSecretAuthenticationFilter = serviceSecretAuthenticationFilter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(authorized -> authorized
                        .requestMatchers("/health","/v3/api-docs", "/docs").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api").authenticated()
                        .anyRequest().permitAll()
                ).addFilterBefore(serviceSecretAuthenticationFilter, SecurityContextHolderFilter.class)
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/health/**", "/docs", "/v3/api-docs");
    }
}
